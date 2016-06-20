package com.novoda.github.reports.batch.aws.issue;

import com.novoda.github.reports.aws.queue.AmazonGetCommentsQueueMessage;
import com.novoda.github.reports.aws.queue.AmazonQueueMessage;
import com.novoda.github.reports.aws.queue.QueueMessage;
import com.novoda.github.reports.service.issue.GithubComment;
import com.novoda.github.reports.service.issue.GithubIssueService;
import com.novoda.github.reports.service.issue.IssueService;
import com.novoda.github.reports.service.issue.RepositoryIssueEvent;
import com.novoda.github.reports.service.issue.RepositoryIssueEventComment;
import com.novoda.github.reports.service.network.DateToISO8601Converter;

import rx.Observable;
import rx.functions.Func3;

public class CommentsServiceClient {

    private static final int DEFAULT_PER_PAGE_COUNT = 100;

    private final IssueService issueService;
    private final DateToISO8601Converter dateConverter;

    public static CommentsServiceClient newInstance() {
        IssueService issueService = GithubIssueService.newInstance();
        DateToISO8601Converter dateConverter = new DateToISO8601Converter();
        return new CommentsServiceClient(issueService, dateConverter);
    }

    private CommentsServiceClient(IssueService issueService, DateToISO8601Converter dateConverter) {
        this.issueService = issueService;
        this.dateConverter = dateConverter;
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
                .compose(TransformToRepositoryIssueEvent.<GithubComment, RepositoryIssueEvent>newInstance(
                        message.repositoryId(),
                        message.issueNumber(),
                        RepositoryIssueEventComment::new
                ))
                .compose(ResponseRepositoryIssueEventPersistTransformer.newInstance())
                .compose(NextMessagesIssueEventTransformer.newInstance(message, buildAmazonGetCommentsQueueMessage()));
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
                amazonGetCommentsQueueMessage.issueNumber()
        );
    }

    private int pageFrom(QueueMessage message) {
        return Math.toIntExact(message.page());
    }

}
