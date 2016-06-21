package com.novoda.github.reports.lambda.issue;

import com.novoda.github.reports.batch.aws.queue.AmazonGetIssuesQueueMessage;
import com.novoda.github.reports.batch.aws.queue.AmazonQueueMessage;
import com.novoda.github.reports.batch.queue.QueueMessage;
import com.novoda.github.reports.data.db.properties.DatabaseCredentialsReader;
import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.service.issue.GithubIssueService;
import com.novoda.github.reports.service.issue.IssueService;
import com.novoda.github.reports.service.network.DateToISO8601Converter;
import com.novoda.github.reports.service.properties.GithubCredentialsReader;

import rx.Observable;

public class IssuesServiceClient {

    private static final int DEFAULT_PER_PAGE_COUNT = 100;
    private static final GithubIssue.State DEFAULT_STATE = GithubIssue.State.ALL;

    private final IssueService issueService;
    private final DateToISO8601Converter dateConverter;
    private final ResponseRepositoryIssuePersistTransformer responseRepositoryIssuePersistTransformer;

    public static IssuesServiceClient newInstance() {
        IssueService issueService = GithubIssueService.newInstance();
        DateToISO8601Converter dateConverter = new DateToISO8601Converter();
        return new IssuesServiceClient(issueService, dateConverter);
    }

    public static IssuesServiceClient newInstance(GithubCredentialsReader githubCredentialsReader,
                                                  DatabaseCredentialsReader databaseCredentialsReader) {

        IssueService issueService = GithubIssueService.newInstance(githubCredentialsReader);
        DateToISO8601Converter dateConverter = new DateToISO8601Converter();
        return new IssuesServiceClient(issueService, dateConverter, databaseCredentialsReader);
    }

    private IssuesServiceClient(IssueService issueService,
                                DateToISO8601Converter dateConverter) {

        this.issueService = issueService;
        this.dateConverter = dateConverter;
        this.responseRepositoryIssuePersistTransformer = ResponseRepositoryIssuePersistTransformer.newInstance();
    }

    private IssuesServiceClient(IssueService issueService,
                                DateToISO8601Converter dateConverter,
                                DatabaseCredentialsReader databaseCredentialsReader) {

        this.issueService = issueService;
        this.dateConverter = dateConverter;
        this.responseRepositoryIssuePersistTransformer = ResponseRepositoryIssuePersistTransformer.newInstance(databaseCredentialsReader);
    }

    public Observable<AmazonQueueMessage> retrieveIssuesFor(AmazonGetIssuesQueueMessage message) {
        String date = dateConverter.toISO8601NoMillisOrNull(message.sinceOrNull());
        return issueService
                .getIssuesFor(
                        message.organisationName(),
                        message.repositoryName(),
                        DEFAULT_STATE,
                        date,
                        pageFrom(message),
                        DEFAULT_PER_PAGE_COUNT
                )
                .compose(TransformToRepositoryIssue.newInstance(message.repositoryId()))
                .compose(responseRepositoryIssuePersistTransformer)
                .compose(NextMessagesIssueTransformer.newInstance(message));
    }

    private int pageFrom(QueueMessage message) {
        return Math.toIntExact(message.page());
    }

}
