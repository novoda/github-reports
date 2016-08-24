package com.novoda.github.reports.lambda.issue;

import com.novoda.github.reports.batch.aws.queue.AmazonGetReactionsQueueMessage;
import com.novoda.github.reports.batch.aws.queue.AmazonQueueMessage;
import com.novoda.github.reports.batch.queue.QueueMessage;
import com.novoda.github.reports.data.db.properties.DatabaseCredentialsReader;
import com.novoda.github.reports.lambda.NextMessagesTransformer;
import com.novoda.github.reports.lambda.persistence.ResponsePersistTransformer;
import com.novoda.github.reports.service.issue.*;
import rx.Observable;
import rx.functions.Func3;

public class ReactionsServiceClient {

    private static final int DEFAULT_PER_PAGE_COUNT = 100;

    private final IssueService issueService;
    private final ResponsePersistTransformer<RepositoryIssueEvent> responseRepositoryIssueEventPersistTransformer;

    public static ReactionsServiceClient newInstance() {
        IssueService issueService = GithubIssueService.newInstance();
        ResponsePersistTransformer<RepositoryIssueEvent> persistRepositoryIssueEventsTransformer =
                ResponseRepositoryIssueEventPersistTransformer.newInstance();

        return new ReactionsServiceClient(issueService, persistRepositoryIssueEventsTransformer);
    }

    public static ReactionsServiceClient newInstance(DatabaseCredentialsReader databaseCredentialsReader) {
        IssueService issueService = GithubIssueService.newInstance();
        ResponsePersistTransformer<RepositoryIssueEvent> persistRepositoryIssueEventsTransformer =
                ResponseRepositoryIssueEventPersistTransformer.newInstance(databaseCredentialsReader);

        return new ReactionsServiceClient(issueService, persistRepositoryIssueEventsTransformer);
    }

    private ReactionsServiceClient(IssueService issueService,
                                   ResponsePersistTransformer<RepositoryIssueEvent> responseRepositoryIssueEventPersistTransformer) {

        this.issueService = issueService;
        this.responseRepositoryIssueEventPersistTransformer = responseRepositoryIssueEventPersistTransformer;
    }

    public Observable<AmazonQueueMessage> retrieveReactionsAsEventsFrom(AmazonGetReactionsQueueMessage message) {
        return issueService
                .getReactionsFor(
                        message.organisationName(),
                        message.repositoryName(),
                        Math.toIntExact(message.issueNumber()),
                        pageFrom(message),
                        DEFAULT_PER_PAGE_COUNT
                )
                .compose(transformToRepositoryIssueEvents(message))
                .compose(responseRepositoryIssueEventPersistTransformer)
                .compose(getNextQueueMessages(message));
    }

    private int pageFrom(QueueMessage message) {
        return Math.toIntExact(message.page());
    }

    private TransformToRepositoryIssueEvent<GithubReaction, RepositoryIssueEventReaction> transformToRepositoryIssueEvents(AmazonGetReactionsQueueMessage message) {
        return new TransformToRepositoryIssueEvent<>(
                message.repositoryId(),
                message.issueNumber(),
                message.issueOwnerId(),
                message.isPullRequest(),
                RepositoryIssueEventReaction::new
        );
    }

    private NextMessagesTransformer<RepositoryIssueEvent, AmazonGetReactionsQueueMessage> getNextQueueMessages(AmazonGetReactionsQueueMessage message) {
        return NextMessagesIssueEventTransformer.newInstance(
                message,
                buildAmazonGetReactionsQueueMessage()
        );
    }

    private Func3<Boolean, Long, AmazonGetReactionsQueueMessage, AmazonGetReactionsQueueMessage> buildAmazonGetReactionsQueueMessage() {
        return (isTerminal, nextPage, amazonGetCommentsQueueMessage) -> AmazonGetReactionsQueueMessage.create(
                isTerminal,
                nextPage,
                amazonGetCommentsQueueMessage.receiptHandle(),
                amazonGetCommentsQueueMessage.organisationName(),
                amazonGetCommentsQueueMessage.sinceOrNull(),
                amazonGetCommentsQueueMessage.repositoryId(),
                amazonGetCommentsQueueMessage.repositoryName(),
                amazonGetCommentsQueueMessage.issueNumber(),
                amazonGetCommentsQueueMessage.issueOwnerId(),
                amazonGetCommentsQueueMessage.isPullRequest()
        );
    }

}
