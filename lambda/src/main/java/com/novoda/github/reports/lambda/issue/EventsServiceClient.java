package com.novoda.github.reports.lambda.issue;

import com.novoda.github.reports.aws.queue.AmazonGetEventsQueueMessage;
import com.novoda.github.reports.aws.queue.AmazonQueueMessage;
import com.novoda.github.reports.aws.queue.QueueMessage;
import com.novoda.github.reports.service.issue.GithubIssueService;
import com.novoda.github.reports.service.issue.IssueService;
import com.novoda.github.reports.service.issue.RepositoryIssueEventEvent;

import rx.Observable;
import rx.functions.Func3;

public class EventsServiceClient {

    private static final int DEFAULT_PER_PAGE_COUNT = 100;

    private final IssueService issueService;

    public static EventsServiceClient newInstance() {
        IssueService issueService = GithubIssueService.newInstance();
        return new EventsServiceClient(issueService);
    }

    private EventsServiceClient(IssueService issueService) {
        this.issueService = issueService;
    }

    public Observable<AmazonQueueMessage> retrieveEventsFrom(AmazonGetEventsQueueMessage message) {
        return issueService
                .getEventsFor(
                        message.organisationName(),
                        message.repositoryName(),
                        Math.toIntExact(message.issueNumber()),
                        pageFrom(message),
                        DEFAULT_PER_PAGE_COUNT
                )
                .compose(new TransformToRepositoryIssueEvent<>(
                        message.repositoryId(),
                        message.issueNumber(),
                        RepositoryIssueEventEvent::newInstance
                ))
                .compose(ResponseRepositoryIssueEventPersistTransformer.newInstance())
                .compose(NextMessagesIssueEventTransformer.newInstance(message, buildAmazonGetEventsQueueMessage()));
    }

    private Func3<Boolean, Long, AmazonGetEventsQueueMessage, AmazonGetEventsQueueMessage> buildAmazonGetEventsQueueMessage() {
        return (isTerminal, nextPage, amazonGetCommentsQueueMessage) -> AmazonGetEventsQueueMessage.create(
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
