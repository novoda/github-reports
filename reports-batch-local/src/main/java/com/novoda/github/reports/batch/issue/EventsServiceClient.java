package com.novoda.github.reports.batch.issue;

import com.novoda.github.reports.batch.retry.RateLimitResetTimerSubject;
import com.novoda.github.reports.batch.retry.RateLimitResetTimerSubjectContainer;
import com.novoda.github.reports.batch.retry.RetryWhenTokenResets;
import com.novoda.github.reports.service.issue.GithubEvent;
import com.novoda.github.reports.service.issue.GithubIssueService;
import com.novoda.github.reports.service.issue.IssueService;
import com.novoda.github.reports.service.issue.RepositoryIssue;
import com.novoda.github.reports.service.issue.RepositoryIssueEvent;
import com.novoda.github.reports.service.issue.RepositoryIssueEventEvent;
import com.novoda.github.reports.service.network.PagedTransformer;
import com.novoda.github.reports.service.network.RateLimitDelayTransformer;
import com.novoda.github.reports.service.persistence.RepositoryIssueEventPersistTransformer;

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

    private final RepositoryIssueEventPersistTransformer repositoryIssueEventPersistTransformer;

    public static EventsServiceClient newInstance() {
        IssueService issueService = GithubIssueService.newCachingInstance();
        RepositoryIssueEventPersistTransformer repositoryIssueEventPersistTransformer = RepositoryIssueEventPersistTransformer.newInstance();
        RateLimitDelayTransformer<GithubEvent> eventRateLimitDelayTransformer = RateLimitDelayTransformer.newInstance();
        RateLimitResetTimerSubject rateLimitResetTimerSubject = RateLimitResetTimerSubjectContainer.getInstance();
        return new EventsServiceClient(issueService,
                                       repositoryIssueEventPersistTransformer,
                                       rateLimitResetTimerSubject,
                                       eventRateLimitDelayTransformer);
    }

    private EventsServiceClient(IssueService issueService,
                                RepositoryIssueEventPersistTransformer repositoryIssueEventPersistTransformer,
                                RateLimitResetTimerSubject rateLimitResetTimerSubject,
                                RateLimitDelayTransformer<GithubEvent> eventRateLimitDelayTransformer) {

        this.issueService = issueService;
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
                .filter(event -> since == null || event.getCreatedAt().after(since))
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

        return issueService.getEventsFor(organisation, repository, issueNumber, page, pageCount)
                .compose(eventRateLimitDelayTransformer)
                .compose(PagedTransformer.newInstance(nextPage -> getPagedEventsFor(organisation, repository, issueNumber, nextPage, pageCount)));
    }

    private boolean shouldStoreEvent(GithubEvent event) {
        return EVENT_TYPES_TO_BE_STORED.contains(event.getType());
    }

}
