package com.novoda.github.reports.batch.issue;

import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.service.network.DateToISO8601Converter;
import com.novoda.github.reports.service.network.PagedTransformer;
import com.novoda.github.reports.service.network.RateLimitDelayTransformer;
import com.novoda.github.reports.service.persistence.ConnectionManagerContainer;
import com.novoda.github.reports.service.persistence.EventUserConverter;
import com.novoda.github.reports.service.persistence.PersistEventTransformer;
import com.novoda.github.reports.service.persistence.PersistEventUserTransformer;
import com.novoda.github.reports.service.persistence.PersistIssueTransformer;
import com.novoda.github.reports.service.persistence.PersistUserTransformer;
import com.novoda.github.reports.service.persistence.converter.Converter;
import com.novoda.github.reports.service.persistence.converter.EventConverter;
import com.novoda.github.reports.service.persistence.converter.IssueConverter;
import com.novoda.github.reports.service.persistence.converter.UserConverter;
import com.novoda.github.reports.service.pullrequest.GithubPullRequestService;
import com.novoda.github.reports.service.pullrequest.PullRequestService;
import com.novoda.github.reports.service.repository.GithubRepository;
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
import com.novoda.github.reports.service.issue.GithubComment;
import com.novoda.github.reports.service.issue.GithubEvent;
import com.novoda.github.reports.service.issue.GithubIssueService;
import com.novoda.github.reports.service.issue.IssueService;
import com.novoda.github.reports.service.issue.RepositoryIssue;
import com.novoda.github.reports.service.issue.RepositoryIssueEvent;
import com.novoda.github.reports.service.issue.RepositoryIssueEventComment;
import com.novoda.github.reports.service.issue.RepositoryIssueEventEvent;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Response;
import rx.Observable;

import static com.novoda.github.reports.service.issue.GithubEvent.Type.*;

public class IssuesServiceClient {

    private static final int DEFAULT_PER_PAGE_COUNT = 100;
    private static final int FIRST_PAGE = 1;
    private static final GithubIssue.State DEFAULT_STATE = GithubIssue.State.ALL;
    private static final Date NO_SINCE_DATE = null;

    private static final Set<GithubEvent.Type> EVENT_TYPES_TO_BE_STORED = new HashSet<>(Arrays.asList(
            COMMENTED,
            CLOSED,
            HEAD_REF_DELETED,
            LABELED,
            MERGED,
            UNLABELED
    ));

    private final IssueService issueService;
    private final PullRequestService pullRequestService;
    private final DateToISO8601Converter dateConverter;

    private final RateLimitDelayTransformer<GithubIssue> issueRateLimitDelayTransformer;
    private final RateLimitDelayTransformer<GithubEvent> eventRateLimitDelayTransformer;
    private final RateLimitDelayTransformer<GithubComment> commentRateLimitDelayTransformer;
    private final RateLimitResetTimerSubject rateLimitResetTimerSubject;

    private final EventDataLayer eventDataLayer;
    private final Converter<RepositoryIssue, Event> issueConverter;
    private final UserDataLayer userDataLayer;
    private final Converter<RepositoryIssue, User> userConverter;
    private final Converter<RepositoryIssueEvent, User> eventUserConverter;
    private final Converter<RepositoryIssueEvent, Event> eventConverter;

    public static IssuesServiceClient newInstance() {
        IssueService issueService = GithubIssueService.newInstance();

        DateToISO8601Converter dateConverter = new DateToISO8601Converter();
        RateLimitDelayTransformer<GithubIssue> issueRateLimitDelayTransformer = RateLimitDelayTransformer.newInstance();
        RateLimitDelayTransformer<GithubEvent> eventRateLimitDelayTransformer = RateLimitDelayTransformer.newInstance();
        RateLimitDelayTransformer<GithubComment> commentRateLimitDelayTransformer = RateLimitDelayTransformer.newInstance();

        PullRequestService pullRequestService = GithubPullRequestService.newInstance();
        ConnectionManager connectionManager = ConnectionManagerContainer.getConnectionManager();

        EventDataLayer eventDataLayer = DbEventDataLayer.newInstance(connectionManager);
        Converter<RepositoryIssue, Event> issueConverter = IssueConverter.newInstance();
        UserDataLayer userDataLayer = DbUserDataLayer.newInstance(connectionManager);
        Converter<RepositoryIssue, User> userConverter = UserConverter.newInstance();
        Converter<RepositoryIssueEvent, User> eventUserConverter = EventUserConverter.newInstance();
        Converter<RepositoryIssueEvent, Event> eventConverter = EventConverter.newInstance();

        RateLimitResetTimerSubject rateLimitResetTimerSubject = RateLimitResetTimerSubjectContainer.getInstance();

        return new IssuesServiceClient(issueService,
                                       pullRequestService,
                                       dateConverter,
                                       issueRateLimitDelayTransformer,
                                       eventRateLimitDelayTransformer,
                                       commentRateLimitDelayTransformer,
                                       rateLimitResetTimerSubject,
                                       eventDataLayer,
                                       issueConverter,
                                       userDataLayer,
                                       userConverter,
                                       eventUserConverter,
                                       eventConverter);
    }

    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    // TODO @RUI breakdown issue service client into issues, events, comments service clients
    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    private IssuesServiceClient(IssueService issueService,
                               PullRequestService pullRequestService,
                               DateToISO8601Converter dateConverter,
                               RateLimitDelayTransformer<GithubIssue> issueRateLimitDelayTransformer,
                               RateLimitDelayTransformer<GithubEvent> eventRateLimitDelayTransformer,
                               RateLimitDelayTransformer<GithubComment> commentRateLimitDelayTransformer,
                               RateLimitResetTimerSubject rateLimitResetTimerSubject,
                               EventDataLayer eventDataLayer,
                               Converter<RepositoryIssue, Event> issueConverter,
                               UserDataLayer userDataLayer,
                               Converter<RepositoryIssue, User> userConverter,
                               Converter<RepositoryIssueEvent, User> eventUserConverter,
                               Converter<RepositoryIssueEvent, Event> eventConverter) {

        this.issueService = issueService;
        this.pullRequestService = pullRequestService;
        this.dateConverter = dateConverter;
        this.issueRateLimitDelayTransformer = issueRateLimitDelayTransformer;
        this.eventRateLimitDelayTransformer = eventRateLimitDelayTransformer;
        this.commentRateLimitDelayTransformer = commentRateLimitDelayTransformer;
        this.rateLimitResetTimerSubject = rateLimitResetTimerSubject;
        this.eventDataLayer = eventDataLayer;
        this.issueConverter = issueConverter;
        this.userDataLayer = userDataLayer;
        this.userConverter = userConverter;
        this.eventUserConverter = eventUserConverter;
        this.eventConverter = eventConverter;
    }

    public Observable<RepositoryIssue> retrieveIssuesFrom(GithubRepository repository, Date since) {
        return getPagedIssuesFor(repository.getOwnerUsername(), repository.getName(), since, FIRST_PAGE, DEFAULT_PER_PAGE_COUNT)
                .flatMapIterable(Response::body)
                .compose(RetryWhenTokenResets.newInstance(rateLimitResetTimerSubject))
                .map(issue -> RepositoryIssue.newInstance(repository, issue))
                .compose(PersistUserTransformer.newInstance(userDataLayer, userConverter))
                .compose(PersistIssueTransformer.newInstance(eventDataLayer, issueConverter));
    }

    private Observable<Response<List<GithubIssue>>> getPagedIssuesFor(String organisation,
                                                                      String repository,
                                                                      Date since,
                                                                      Integer page,
                                                                      Integer pageCount) {

        String date = dateConverter.toISO8601NoMillisOrNull(since);
        return issueService.getIssuesFor(organisation, repository, DEFAULT_STATE, date, page, pageCount)
                .compose(issueRateLimitDelayTransformer)
                .compose(PagedTransformer.newInstance(nextPage -> getPagedIssuesFor(organisation, repository, since, nextPage, pageCount)));
    }

    public Observable<RepositoryIssueEvent> retrieveEventsFrom(RepositoryIssue repositoryIssue) {
        return retrieveEventsFrom(repositoryIssue, NO_SINCE_DATE);
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

    public Observable<RepositoryIssueEvent> retrieveCommentsFrom(RepositoryIssue repositoryIssue, Date since) {
        return Observable.merge(
                        retrieveCommentsFromIssue(repositoryIssue, since),
                        retrieveReviewCommentsFromPullRequest(repositoryIssue, since))
                .map(comment -> RepositoryIssueEventComment.newInstance(repositoryIssue, comment))
                .compose(PersistEventUserTransformer.newInstance(userDataLayer, eventUserConverter))
                .compose(PersistEventTransformer.newInstance(eventDataLayer, eventConverter));
    }

    private Observable<GithubComment> retrieveCommentsFromIssue(RepositoryIssue repositoryIssue, Date since) {
        String organisation = repositoryIssue.getOwnerUsername();
        String repository = repositoryIssue.getRepositoryName();
        int issueNumber = repositoryIssue.getIssueNumber();
        return getPagedCommentsFor(organisation, repository, issueNumber, since, FIRST_PAGE, DEFAULT_PER_PAGE_COUNT)
                .flatMapIterable(Response::body)
                .compose(RetryWhenTokenResets.newInstance(rateLimitResetTimerSubject));
    }

    private Observable<Response<List<GithubComment>>> getPagedCommentsFor(String organisation,
                                                                          String repository,
                                                                          Integer issueNumber,
                                                                          Date since,
                                                                          Integer page,
                                                                          Integer pageCount) {

        String date = dateConverter.toISO8601NoMillisOrNull(since);
        return issueService.getCommentsFor(organisation, repository, issueNumber, date, page, pageCount)
                .compose(commentRateLimitDelayTransformer)
                .compose(PagedTransformer.newInstance(nextPage -> getPagedCommentsFor(
                        organisation,
                        repository,
                        issueNumber,
                        since,
                        nextPage,
                        pageCount
                )));
    }

    private Observable<GithubComment> retrieveReviewCommentsFromPullRequest(RepositoryIssue repositoryIssue, Date since) {
        if (isNotPullRequest(repositoryIssue)) {
            return Observable.empty();
        }
        String organisation = repositoryIssue.getOwnerUsername();
        String repository = repositoryIssue.getRepositoryName();
        int issueNumber = repositoryIssue.getIssueNumber();
        return pullRequestService
                .getReviewCommentsForPullRequestFor(organisation, repository, issueNumber, since)
                .compose(RetryWhenTokenResets.newInstance(rateLimitResetTimerSubject));
    }

    private boolean isNotPullRequest(RepositoryIssue repositoryIssue) {
        return !repositoryIssue.isPullRequest();
    }
}
