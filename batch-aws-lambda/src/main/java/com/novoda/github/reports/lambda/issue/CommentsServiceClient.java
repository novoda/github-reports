package com.novoda.github.reports.lambda.issue;

import com.novoda.github.reports.batch.aws.queue.AmazonGetCommentsQueueMessage;
import com.novoda.github.reports.batch.aws.queue.AmazonQueueMessage;
import com.novoda.github.reports.batch.queue.QueueMessage;
import com.novoda.github.reports.data.db.properties.DatabaseCredentialsReader;
import com.novoda.github.reports.service.issue.GithubIssueService;
import com.novoda.github.reports.service.issue.IssueService;
import com.novoda.github.reports.service.issue.RepositoryIssueEventComment;
import com.novoda.github.reports.service.network.DateToISO8601Converter;
import com.novoda.github.reports.service.properties.GithubCredentialsReader;

import rx.Observable;
import rx.functions.Func3;

public class CommentsServiceClient {

    private static final int DEFAULT_PER_PAGE_COUNT = 100;

    private final IssueService issueService;
    private final DateToISO8601Converter dateConverter;
    private final ResponseRepositoryIssueEventPersistTransformer responseRepositoryIssueEventPersistTransformer;

    public static CommentsServiceClient newInstance() {
        IssueService issueService = GithubIssueService.newInstance();
        DateToISO8601Converter dateConverter = new DateToISO8601Converter();
        return new CommentsServiceClient(issueService, dateConverter);
    }

    public static CommentsServiceClient newInstance(GithubCredentialsReader githubCredentialsReader,
                                                    DatabaseCredentialsReader databaseCredentialsReader) {

        IssueService issueService = GithubIssueService.newInstance(githubCredentialsReader);
        DateToISO8601Converter dateConverter = new DateToISO8601Converter();
        return new CommentsServiceClient(issueService, dateConverter, databaseCredentialsReader);
    }

    private CommentsServiceClient(IssueService issueService, DateToISO8601Converter dateConverter) {
        this.issueService = issueService;
        this.dateConverter = dateConverter;
        this.responseRepositoryIssueEventPersistTransformer = ResponseRepositoryIssueEventPersistTransformer.newInstance();
    }

    private CommentsServiceClient(IssueService issueService,
                                  DateToISO8601Converter dateConverter,
                                  DatabaseCredentialsReader databaseCredentialsReader) {

        this.issueService = issueService;
        this.dateConverter = dateConverter;
        this.responseRepositoryIssueEventPersistTransformer = ResponseRepositoryIssueEventPersistTransformer.newInstance(databaseCredentialsReader);
    }

    public Observable<AmazonQueueMessage> retrieveCommentsAsEventsFrom(AmazonGetCommentsQueueMessage message) {
        String date = dateConverter.toISO8601NoMillisOrNull(message.sinceOrNull());

        return issueService
                .getCommentsFor(
                        message.organisationName(),
                        message.repositoryName(),
                        Math.toIntExact(message.issueNumber()),
                        date,
                        pageFrom(message),
                        DEFAULT_PER_PAGE_COUNT
                )
                .compose(new TransformToRepositoryIssueEvent<>(
                        message.repositoryId(),
                        message.issueNumber(),
                        message.issueOwnerId(),
                        RepositoryIssueEventComment::new
                ))
                .compose(responseRepositoryIssueEventPersistTransformer)
                .compose(NextMessagesIssueEventTransformer.newInstance(message,
                                                                       buildAmazonGetCommentsQueueMessage()));
    }

    private Func3<Boolean, Long, AmazonGetCommentsQueueMessage, AmazonGetCommentsQueueMessage> buildAmazonGetCommentsQueueMessage() {
        return (isTerminal, nextPage, amazonGetCommentsQueueMessage) -> AmazonGetCommentsQueueMessage.create(
                isTerminal,
                nextPage,
                amazonGetCommentsQueueMessage.receiptHandle(),
                amazonGetCommentsQueueMessage.organisationName(),
                amazonGetCommentsQueueMessage.sinceOrNull(),
                amazonGetCommentsQueueMessage.repositoryId(),
                amazonGetCommentsQueueMessage.repositoryName(),
                amazonGetCommentsQueueMessage.issueNumber(),
                amazonGetCommentsQueueMessage.issueOwnerId()
        );
    }

    private int pageFrom(QueueMessage message) {
        return Math.toIntExact(message.page());
    }

}
