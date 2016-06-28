package com.novoda.github.reports.lambda.issue;

import com.novoda.github.reports.batch.aws.queue.AmazonGetEventsQueueMessage;
import com.novoda.github.reports.batch.aws.queue.AmazonQueueMessage;
import com.novoda.github.reports.batch.queue.QueueMessage;
import com.novoda.github.reports.data.db.properties.DatabaseCredentialsReader;
import com.novoda.github.reports.lambda.NextMessagesTransformer;
import com.novoda.github.reports.lambda.persistence.ResponsePersistTransformer;
import com.novoda.github.reports.service.issue.GithubEvent;
import com.novoda.github.reports.service.issue.GithubIssueService;
import com.novoda.github.reports.service.issue.IssueService;
import com.novoda.github.reports.service.issue.RepositoryIssueEvent;
import com.novoda.github.reports.service.issue.RepositoryIssueEventEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import okhttp3.Headers;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func3;

import static com.novoda.github.reports.service.issue.GithubEvent.Type.*;

public class EventsServiceClient {

    private static final int DEFAULT_PER_PAGE_COUNT = 100;
    private static final Set<GithubEvent.Type> EVENT_TYPES_TO_BE_STORED = new HashSet<>(Arrays.asList(
            COMMENTED,
            CLOSED,
            HEAD_REF_DELETED,
            LABELED,
            MERGED,
            UNLABELED
    ));

    private final IssueService issueService;
    private final ResponsePersistTransformer<RepositoryIssueEvent> responseRepositoryIssueEventPersistTransformer;

    public static EventsServiceClient newInstance() {
        IssueService issueService = GithubIssueService.newInstance();
        return new EventsServiceClient(issueService, ResponseRepositoryIssueEventPersistTransformer.newInstance());
    }

    public static EventsServiceClient newInstance(DatabaseCredentialsReader databaseCredentialsReader) {
        IssueService issueService = GithubIssueService.newInstance();
        ResponsePersistTransformer<RepositoryIssueEvent> responseRepositoryIssueEventPersistTransformer =
                ResponseRepositoryIssueEventPersistTransformer.newInstance(databaseCredentialsReader);

        return new EventsServiceClient(issueService, responseRepositoryIssueEventPersistTransformer);
    }

    private EventsServiceClient(IssueService issueService,
                                ResponsePersistTransformer<RepositoryIssueEvent> responseRepositoryIssueEventPersistTransformer) {

        this.issueService = issueService;
        this.responseRepositoryIssueEventPersistTransformer = responseRepositoryIssueEventPersistTransformer;
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
                .map(filterInterestingEvents())
                .compose(transformToRepositoryIssueEvents(message))
                .compose(responseRepositoryIssueEventPersistTransformer)
                .compose(getNextQueueMessages(message));
    }

    private int pageFrom(QueueMessage message) {
        return Math.toIntExact(message.page());
    }

    private Func1<Response<List<GithubEvent>>, Response<List<GithubEvent>>> filterInterestingEvents() {
        return response -> {
            Headers headers = response.headers();
            List<GithubEvent> body = response.body().stream()
                    .filter(this::shouldStoreEvent)
                    .collect(Collectors.toList());
            return Response.success(body, headers);
        };
    }

    private boolean shouldStoreEvent(GithubEvent event) {
        return EVENT_TYPES_TO_BE_STORED.contains(event.getType());
    }

    private TransformToRepositoryIssueEvent<GithubEvent, RepositoryIssueEventEvent> transformToRepositoryIssueEvents(AmazonGetEventsQueueMessage message) {
        return new TransformToRepositoryIssueEvent<>(
                message.repositoryId(),
                message.issueNumber(),
                message.issueOwnerId(),
                message.isPullRequest(),
                RepositoryIssueEventEvent::newInstance
        );
    }

    private NextMessagesTransformer<RepositoryIssueEvent, AmazonGetEventsQueueMessage> getNextQueueMessages(AmazonGetEventsQueueMessage message) {
        return NextMessagesIssueEventTransformer.newInstance(message, buildAmazonGetEventsQueueMessage());
    }

    private Func3<Boolean, Long, AmazonGetEventsQueueMessage, AmazonGetEventsQueueMessage> buildAmazonGetEventsQueueMessage() {
        return (isTerminal, nextPage, amazonGetEventsQueueMessage) -> AmazonGetEventsQueueMessage.create(
                isTerminal,
                nextPage,
                amazonGetEventsQueueMessage.receiptHandle(),
                amazonGetEventsQueueMessage.organisationName(),
                amazonGetEventsQueueMessage.sinceOrNull(),
                amazonGetEventsQueueMessage.repositoryId(),
                amazonGetEventsQueueMessage.repositoryName(),
                amazonGetEventsQueueMessage.issueNumber(),
                amazonGetEventsQueueMessage.issueOwnerId(),
                amazonGetEventsQueueMessage.isPullRequest()
        );
    }

}
