package com.novoda.github.reports.lambda.pullrequest;

import com.novoda.github.reports.batch.aws.queue.AmazonGetReviewCommentsQueueMessage;
import com.novoda.github.reports.batch.aws.queue.AmazonQueueMessage;
import com.novoda.github.reports.batch.queue.QueueMessage;
import com.novoda.github.reports.data.db.properties.DatabaseCredentialsReader;
import com.novoda.github.reports.lambda.NextMessagesTransformer;
import com.novoda.github.reports.lambda.issue.NextMessagesIssueEventTransformer;
import com.novoda.github.reports.lambda.issue.ResponseRepositoryIssueEventPersistTransformer;
import com.novoda.github.reports.lambda.issue.TransformToRepositoryIssueEvent;
import com.novoda.github.reports.lambda.persistence.ResponsePersistTransformer;
import com.novoda.github.reports.service.issue.GithubComment;
import com.novoda.github.reports.service.issue.RepositoryIssueEvent;
import com.novoda.github.reports.service.issue.RepositoryIssueEventComment;
import com.novoda.github.reports.service.network.DateToISO8601Converter;
import com.novoda.github.reports.service.network.GithubApiService;
import com.novoda.github.reports.service.network.GithubCachingServiceContainer;

import rx.Observable;
import rx.functions.Func3;

public class ReviewCommentsServiceClient {

    private static final int DEFAULT_PER_PAGE_COUNT = 100;

    private final GithubApiService apiService;
    private final DateToISO8601Converter dateConverter;
    private final ResponsePersistTransformer<RepositoryIssueEvent> responseRepositoryIssueEventPersistTransformer;

    public static ReviewCommentsServiceClient newInstance() {
        GithubApiService apiService = GithubCachingServiceContainer.getGithubService();
        DateToISO8601Converter dateConverter = new DateToISO8601Converter();
        ResponsePersistTransformer<RepositoryIssueEvent> responseRepositoryIssueEventPersistTransformer =
                ResponseRepositoryIssueEventPersistTransformer.newInstance();

        return new ReviewCommentsServiceClient(apiService, dateConverter, responseRepositoryIssueEventPersistTransformer);
    }

    public static ReviewCommentsServiceClient newInstance(DatabaseCredentialsReader databaseCredentialsReader) {

        GithubApiService apiService = GithubCachingServiceContainer.getGithubService();
        DateToISO8601Converter dateConverter = new DateToISO8601Converter();
        ResponsePersistTransformer<RepositoryIssueEvent> responseRepositoryIssueEventPersistTransformer =
                ResponseRepositoryIssueEventPersistTransformer.newInstance(databaseCredentialsReader);

        return new ReviewCommentsServiceClient(apiService, dateConverter, responseRepositoryIssueEventPersistTransformer);
    }

    private ReviewCommentsServiceClient(GithubApiService apiService,
                                        DateToISO8601Converter dateConverter,
                                        ResponsePersistTransformer<RepositoryIssueEvent> responseRepositoryIssueEventPersistTransformer) {

        this.apiService = apiService;
        this.dateConverter = dateConverter;
        this.responseRepositoryIssueEventPersistTransformer = responseRepositoryIssueEventPersistTransformer;
    }

    public Observable<AmazonQueueMessage> retrieveReviewCommentsFromPullRequest(AmazonGetReviewCommentsQueueMessage message) {
        String date = dateConverter.toISO8601NoMillisOrNull(message.sinceOrNull());
        return apiService
                .getReviewCommentsResponseForPullRequestAndPage(
                        message.organisationName(),
                        message.repositoryName(),
                        issueNumberFrom(message),
                        date,
                        pageFrom(message),
                        DEFAULT_PER_PAGE_COUNT
                )
                .compose(transformToRepositoryIssueEvents(message))
                .compose(responseRepositoryIssueEventPersistTransformer)
                .compose(getNextQueueMessages(message));
    }

    private TransformToRepositoryIssueEvent<GithubComment, RepositoryIssueEventComment> transformToRepositoryIssueEvents(AmazonGetReviewCommentsQueueMessage message) {
        return new TransformToRepositoryIssueEvent<>(
                message.repositoryId(),
                message.issueNumber(),
                message.issueOwnerId(),
                message.isPullRequest(),
                RepositoryIssueEventComment::new
        );
    }

    private int issueNumberFrom(AmazonGetReviewCommentsQueueMessage message) {
        return Math.toIntExact(message.issueNumber());
    }

    private int pageFrom(QueueMessage message) {
        return Math.toIntExact(message.page());
    }

    private Func3<Boolean, Long, AmazonGetReviewCommentsQueueMessage, AmazonGetReviewCommentsQueueMessage> buildAmazonGetReviewCommentsQueueMessage() {

        return (isTerminal, nextPage, amazonGetReviewCommentsQueueMessage) -> AmazonGetReviewCommentsQueueMessage.create(
                isTerminal,
                nextPage,
                amazonGetReviewCommentsQueueMessage.receiptHandle(),
                amazonGetReviewCommentsQueueMessage.organisationName(),
                amazonGetReviewCommentsQueueMessage.sinceOrNull(),
                amazonGetReviewCommentsQueueMessage.repositoryId(),
                amazonGetReviewCommentsQueueMessage.repositoryName(),
                amazonGetReviewCommentsQueueMessage.issueNumber(),
                amazonGetReviewCommentsQueueMessage.issueOwnerId(),
                amazonGetReviewCommentsQueueMessage.isPullRequest()
        );
    }

    private NextMessagesTransformer<RepositoryIssueEvent, AmazonGetReviewCommentsQueueMessage> getNextQueueMessages(AmazonGetReviewCommentsQueueMessage message) {
        return NextMessagesIssueEventTransformer.newInstance(message, buildAmazonGetReviewCommentsQueueMessage());
    }

}
