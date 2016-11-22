package com.novoda.github.reports.lambda.issue;

import com.novoda.github.reports.batch.aws.queue.AmazonGetIssuesQueueMessage;
import com.novoda.github.reports.batch.aws.queue.AmazonQueueMessage;
import com.novoda.github.reports.batch.queue.QueueMessage;
import com.novoda.github.reports.data.db.properties.DatabaseCredentialsReader;
import com.novoda.github.reports.lambda.persistence.ResponsePersistTransformer;
import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.service.issue.RepositoryIssue;
import com.novoda.github.reports.service.network.DateToISO8601Converter;
import com.novoda.github.reports.service.network.GithubApiService;
import com.novoda.github.reports.service.network.GithubServiceContainer;

import rx.Observable;

public class IssuesServiceClient {

    private static final int DEFAULT_PER_PAGE_COUNT = 100;
    private static final GithubIssue.State DEFAULT_STATE = GithubIssue.State.ALL;

    private final GithubApiService apiService;
    private final DateToISO8601Converter dateConverter;
    private final ResponsePersistTransformer<RepositoryIssue> responseRepositoryIssuePersistTransformer;

    public static IssuesServiceClient newInstance() {
        GithubApiService apiService = GithubServiceContainer.getGithubService();
        DateToISO8601Converter dateConverter = new DateToISO8601Converter();
        ResponsePersistTransformer<RepositoryIssue> responseRepositoryIssuePersistTransformer =
                ResponseRepositoryIssuePersistTransformer.newInstance();

        return new IssuesServiceClient(apiService, dateConverter, responseRepositoryIssuePersistTransformer);
    }

    public static IssuesServiceClient newInstance(DatabaseCredentialsReader databaseCredentialsReader) {
        GithubApiService apiService = GithubServiceContainer.getGithubService();
        DateToISO8601Converter dateConverter = new DateToISO8601Converter();
        ResponsePersistTransformer<RepositoryIssue> responseRepositoryIssuePersistTransformer =
                ResponseRepositoryIssuePersistTransformer.newInstance(databaseCredentialsReader);

        return new IssuesServiceClient(apiService, dateConverter, responseRepositoryIssuePersistTransformer);
    }

    private IssuesServiceClient(GithubApiService apiService,
                                DateToISO8601Converter dateConverter,
                                ResponsePersistTransformer<RepositoryIssue> responseRepositoryIssuePersistTransformer) {

        this.apiService = apiService;
        this.dateConverter = dateConverter;
        this.responseRepositoryIssuePersistTransformer = responseRepositoryIssuePersistTransformer;
    }

    public Observable<AmazonQueueMessage> retrieveIssuesFor(AmazonGetIssuesQueueMessage message) {
        String date = dateConverter.toISO8601NoMillisOrNull(message.sinceOrNull());
        return apiService
                .getIssuesResponseForPage(
                        message.organisationName(),
                        message.repositoryName(),
                        DEFAULT_STATE,
                        date,
                        pageFrom(message),
                        DEFAULT_PER_PAGE_COUNT
                )
                .compose(transformToRepositoryIssues(message))
                .compose(responseRepositoryIssuePersistTransformer)
                .compose(getNextQueueMessages(message));
    }

    private int pageFrom(QueueMessage message) {
        return Math.toIntExact(message.page());
    }

    private TransformToRepositoryIssue transformToRepositoryIssues(AmazonGetIssuesQueueMessage message) {
        return TransformToRepositoryIssue.newInstance(message.repositoryId());
    }

    private NextMessagesIssueTransformer getNextQueueMessages(AmazonGetIssuesQueueMessage message) {
        return NextMessagesIssueTransformer.newInstance(message);
    }

}
