package com.novoda.github.reports.lambda.issue;

import com.novoda.github.reports.batch.aws.queue.AmazonGetEventsQueueMessage;
import com.novoda.github.reports.batch.aws.queue.AmazonQueueMessage;
import com.novoda.github.reports.batch.queue.QueueMessage;
import com.novoda.github.reports.data.db.properties.DatabaseCredentialsReader;
import com.novoda.github.reports.service.issue.GithubEvent;
import com.novoda.github.reports.service.issue.GithubIssueService;
import com.novoda.github.reports.service.issue.IssueService;
import com.novoda.github.reports.service.issue.RepositoryIssueEventEvent;
import com.novoda.github.reports.service.properties.GithubCredentialsReader;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private final ResponseRepositoryIssueEventPersistTransformer responseRepositoryIssueEventPersistTransformer;

    public static EventsServiceClient newInstance() {
        IssueService issueService = GithubIssueService.newInstance();
        return new EventsServiceClient(issueService);
    }

    public static EventsServiceClient newInstance(GithubCredentialsReader githubCredentialsReader,
                                                  DatabaseCredentialsReader databaseCredentialsReader) {
        IssueService issueService = GithubIssueService.newInstance(githubCredentialsReader);
        return new EventsServiceClient(issueService, databaseCredentialsReader);
    }

    private EventsServiceClient(IssueService issueService) {
        this.issueService = issueService;
        this.responseRepositoryIssueEventPersistTransformer = ResponseRepositoryIssueEventPersistTransformer.newInstance();
    }

    private EventsServiceClient(IssueService issueService, DatabaseCredentialsReader databaseCredentialsReader) {
        this.issueService = issueService;
        this.responseRepositoryIssueEventPersistTransformer = ResponseRepositoryIssueEventPersistTransformer.newInstance(databaseCredentialsReader);
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
                .compose(new TransformToRepositoryIssueEvent<>(
                        message.repositoryId(),
                        message.issueNumber(),
                        message.issueOwnerId(),
                        RepositoryIssueEventEvent::newInstance
                ))
                .compose(responseRepositoryIssueEventPersistTransformer)
                .compose(NextMessagesIssueEventTransformer.newInstance(message, buildAmazonGetEventsQueueMessage()));
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
        if (event == null || event.getType() == null) {
            Logger.getGlobal().log(Level.WARNING, "null WTF");
            return false;
        }
        return EVENT_TYPES_TO_BE_STORED.contains(event.getType());
    }

    private int pageFrom(QueueMessage message) {
        return Math.toIntExact(message.page());
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
                amazonGetEventsQueueMessage.issueOwnerId()
        );
    }

}
