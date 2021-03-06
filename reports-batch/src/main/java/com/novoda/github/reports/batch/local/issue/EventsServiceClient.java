package com.novoda.github.reports.batch.local.issue;

import com.novoda.github.reports.batch.local.retry.RateLimitResetTimerSubject;
import com.novoda.github.reports.batch.local.retry.RateLimitResetTimerSubjectContainer;
import com.novoda.github.reports.batch.local.retry.RetryWhenTokenResets;
import com.novoda.github.reports.service.issue.GithubEvent;
import com.novoda.github.reports.service.issue.RepositoryIssue;
import com.novoda.github.reports.service.issue.RepositoryIssueEvent;
import com.novoda.github.reports.service.issue.RepositoryIssueEventEvent;
import com.novoda.github.reports.service.network.GithubApiService;
import com.novoda.github.reports.service.network.GithubCachingServiceContainer;
import com.novoda.github.reports.service.network.PagedTransformer;
import com.novoda.github.reports.service.network.RateLimitDelayTransformer;
import com.novoda.github.reports.service.persistence.RepositoryIssueEventPersistTransformer;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Func1;

import java.util.*;

import static com.novoda.github.reports.service.issue.GithubEvent.Type.*;

public class EventsServiceClient {

    private static final int DEFAULT_PER_PAGE_COUNT = 100;
    private static final int FIRST_PAGE = 1;

    private static final Set<GithubEvent.Type> EVENT_TYPES_TO_BE_STORED = new HashSet<>(Arrays.asList(
            CLOSED,
            HEAD_REF_DELETED,
            LABELED,
            MERGED,
            UNLABELED
    ));

    private final GithubApiService apiService;

    private final RateLimitDelayTransformer<GithubEvent> eventRateLimitDelayTransformer;
    private final RateLimitResetTimerSubject rateLimitResetTimerSubject;

    private final RepositoryIssueEventPersistTransformer repositoryIssueEventPersistTransformer;

    public static EventsServiceClient newInstance() {
        GithubApiService apiService = GithubCachingServiceContainer.getGithubService();
        RepositoryIssueEventPersistTransformer repositoryIssueEventPersistTransformer = RepositoryIssueEventPersistTransformer.newInstance();
        RateLimitDelayTransformer<GithubEvent> eventRateLimitDelayTransformer = RateLimitDelayTransformer.newInstance();
        RateLimitResetTimerSubject rateLimitResetTimerSubject = RateLimitResetTimerSubjectContainer.getInstance();
        return new EventsServiceClient(apiService,
                                       repositoryIssueEventPersistTransformer,
                                       rateLimitResetTimerSubject,
                                       eventRateLimitDelayTransformer);
    }

    private EventsServiceClient(GithubApiService apiService,
                                RepositoryIssueEventPersistTransformer repositoryIssueEventPersistTransformer,
                                RateLimitResetTimerSubject rateLimitResetTimerSubject,
                                RateLimitDelayTransformer<GithubEvent> eventRateLimitDelayTransformer) {

        this.apiService = apiService;
        this.repositoryIssueEventPersistTransformer = repositoryIssueEventPersistTransformer;
        this.eventRateLimitDelayTransformer = eventRateLimitDelayTransformer;
        this.rateLimitResetTimerSubject = rateLimitResetTimerSubject;
    }

    public Observable<RepositoryIssueEvent> retrieveEventsFrom(RepositoryIssue repositoryIssue, Date since) {
        return getPagedEventsFor(repositoryIssue.getOwnerUsername(),
                                 repositoryIssue.getRepositoryName(),
                                 repositoryIssue.getIssueNumber(),
                                 FIRST_PAGE,
                                 DEFAULT_PER_PAGE_COUNT)
                .flatMapIterable(Response::body)
                .filter(onlyCreatedAfter(since))
                .compose(RetryWhenTokenResets.newInstance(rateLimitResetTimerSubject))
                .filter(this::shouldStoreEvent)
                .map(event -> RepositoryIssueEventEvent.newInstance(repositoryIssue, event))
                .compose(repositoryIssueEventPersistTransformer);
    }

    private Observable<Response<List<GithubEvent>>> getPagedEventsFor(String organisation,
                                                                      String repository,
                                                                      int issueNumber,
                                                                      int page,
                                                                      int pageCount) {

        return apiService.getEventsResponseForIssueAndPage(organisation, repository, issueNumber, page, pageCount)
                .compose(eventRateLimitDelayTransformer)
                .compose(PagedTransformer.newInstance(nextPage -> getPagedEventsFor(organisation, repository, issueNumber, nextPage, pageCount)));
    }

    private Func1<GithubEvent, Boolean> onlyCreatedAfter(Date since) {
        return event -> since == null || event.getCreatedAt().after(since);
    }

    private boolean shouldStoreEvent(GithubEvent event) {
        return EVENT_TYPES_TO_BE_STORED.contains(event.getType());
    }

}
