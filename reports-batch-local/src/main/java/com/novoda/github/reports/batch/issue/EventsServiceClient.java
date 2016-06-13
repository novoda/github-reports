package com.novoda.github.reports.batch.issue;

import com.novoda.github.reports.batch.retry.RateLimitResetTimerSubject;
import com.novoda.github.reports.batch.retry.RateLimitResetTimerSubjectContainer;
import com.novoda.github.reports.batch.retry.RetryWhenTokenResets;
import com.novoda.github.reports.data.EventDataLayer;
import com.novoda.github.reports.data.UserDataLayer;
import com.novoda.github.reports.data.db.ConnectionManager;
import com.novoda.github.reports.data.db.DbEventDataLayer;
import com.novoda.github.reports.data.db.DbUserDataLayer;
import com.novoda.github.reports.data.model.Event;
import com.novoda.github.reports.data.model.User;
import com.novoda.github.reports.service.issue.GithubEvent;
import com.novoda.github.reports.service.issue.GithubIssueService;
import com.novoda.github.reports.service.issue.IssueService;
import com.novoda.github.reports.service.issue.RepositoryIssue;
import com.novoda.github.reports.service.issue.RepositoryIssueEvent;
import com.novoda.github.reports.service.issue.RepositoryIssueEventEvent;
import com.novoda.github.reports.service.network.PagedTransformer;
import com.novoda.github.reports.service.network.RateLimitDelayTransformer;
import com.novoda.github.reports.service.persistence.ConnectionManagerContainer;
import com.novoda.github.reports.service.persistence.EventUserConverter;
import com.novoda.github.reports.service.persistence.PersistEventTransformer;
import com.novoda.github.reports.service.persistence.PersistEventUserTransformer;
import com.novoda.github.reports.service.persistence.converter.Converter;
import com.novoda.github.reports.service.persistence.converter.EventConverter;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Response;
import rx.Observable;

import static com.novoda.github.reports.service.issue.GithubEvent.Type.*;

public class EventsServiceClient {

    private static final int DEFAULT_PER_PAGE_COUNT = 100;
    private static final int FIRST_PAGE = 1;

    private static final Set<GithubEvent.Type> EVENT_TYPES_TO_BE_STORED = new HashSet<>(Arrays.asList(
            COMMENTED,
            CLOSED,
            HEAD_REF_DELETED,
            LABELED,
            MERGED,
            UNLABELED
    ));

    private final IssueService issueService;

    private final RateLimitDelayTransformer<GithubEvent> eventRateLimitDelayTransformer;
    private final RateLimitResetTimerSubject rateLimitResetTimerSubject;

    private final EventDataLayer eventDataLayer;
    private final UserDataLayer userDataLayer;
    private final Converter<RepositoryIssueEvent, User> eventUserConverter;
    private final Converter<RepositoryIssueEvent, Event> eventConverter;

    public static EventsServiceClient newInstance() {
        IssueService issueService = GithubIssueService.newCachingInstance();

        RateLimitDelayTransformer<GithubEvent> eventRateLimitDelayTransformer = RateLimitDelayTransformer.newInstance();

        ConnectionManager connectionManager = ConnectionManagerContainer.getConnectionManager();
        EventDataLayer eventDataLayer = DbEventDataLayer.newInstance(connectionManager);
        UserDataLayer userDataLayer = DbUserDataLayer.newInstance(connectionManager);
        Converter<RepositoryIssueEvent, User> eventUserConverter = EventUserConverter.newInstance();
        Converter<RepositoryIssueEvent, Event> eventConverter = EventConverter.newInstance();

        RateLimitResetTimerSubject rateLimitResetTimerSubject = RateLimitResetTimerSubjectContainer.getInstance();

        return new EventsServiceClient(issueService,
                                       eventDataLayer, userDataLayer, eventUserConverter, eventConverter, rateLimitResetTimerSubject, eventRateLimitDelayTransformer
        );
    }

    private EventsServiceClient(IssueService issueService,
                                EventDataLayer eventDataLayer,
                                UserDataLayer userDataLayer,
                                Converter<RepositoryIssueEvent, User> eventUserConverter,
                                Converter<RepositoryIssueEvent, Event> eventConverter,
                                RateLimitResetTimerSubject rateLimitResetTimerSubject,
                                RateLimitDelayTransformer<GithubEvent> eventRateLimitDelayTransformer) {

        this.issueService = issueService;
        this.eventRateLimitDelayTransformer = eventRateLimitDelayTransformer;
        this.rateLimitResetTimerSubject = rateLimitResetTimerSubject;
        this.eventDataLayer = eventDataLayer;
        this.userDataLayer = userDataLayer;
        this.eventUserConverter = eventUserConverter;
        this.eventConverter = eventConverter;
    }

    public Observable<RepositoryIssueEvent> retrieveEventsFrom(RepositoryIssue repositoryIssue, Date since) {
        return getPagedEventsFor(repositoryIssue.getOwnerUsername(),
                                 repositoryIssue.getRepositoryName(),
                                 repositoryIssue.getIssueNumber(),
                                 FIRST_PAGE,
                                 DEFAULT_PER_PAGE_COUNT)
                .flatMapIterable(Response::body)
                .filter(event -> since == null || event.getCreatedAt().after(since))
                .compose(RetryWhenTokenResets.newInstance(rateLimitResetTimerSubject))
                .filter(this::shouldStoreEvent)
                .map(event -> RepositoryIssueEventEvent.newInstance(repositoryIssue, event))
                .compose(PersistEventUserTransformer.newInstance(userDataLayer, eventUserConverter))
                .compose(PersistEventTransformer.newInstance(eventDataLayer, eventConverter));
    }

    private Observable<Response<List<GithubEvent>>> getPagedEventsFor(String organisation,
                                                                      String repository,
                                                                      int issueNumber,
                                                                      int page,
                                                                      int pageCount) {

        return issueService.getEventsFor(organisation, repository, issueNumber, page, pageCount)
                .compose(eventRateLimitDelayTransformer)
                .compose(PagedTransformer.newInstance(nextPage -> getPagedEventsFor(organisation, repository, issueNumber, nextPage, pageCount)));
    }

    private boolean shouldStoreEvent(GithubEvent event) {
        return EVENT_TYPES_TO_BE_STORED.contains(event.getType());
    }

}
