package com.novoda.github.reports.batch.issue;

import com.novoda.github.reports.batch.pullrequest.PullRequestServiceClient;
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
import com.novoda.github.reports.service.issue.GithubIssueService;
import com.novoda.github.reports.service.issue.IssueService;
import com.novoda.github.reports.service.issue.RepositoryIssue;
import com.novoda.github.reports.service.issue.RepositoryIssueEvent;
import com.novoda.github.reports.service.issue.RepositoryIssueEventComment;
import com.novoda.github.reports.service.network.DateToISO8601Converter;
import com.novoda.github.reports.service.network.PagedTransformer;
import com.novoda.github.reports.service.network.RateLimitDelayTransformer;
import com.novoda.github.reports.service.persistence.ConnectionManagerContainer;
import com.novoda.github.reports.service.persistence.EventUserConverter;
import com.novoda.github.reports.service.persistence.PersistEventTransformer;
import com.novoda.github.reports.service.persistence.PersistEventUserTransformer;
import com.novoda.github.reports.service.persistence.converter.Converter;
import com.novoda.github.reports.service.persistence.converter.EventConverter;

import java.util.Date;
import java.util.List;

import retrofit2.Response;
import rx.Observable;

public class CommentsServiceClient {

    private static final int DEFAULT_PER_PAGE_COUNT = 100;
    private static final int FIRST_PAGE = 1;

    private final IssueService issueService;
    private final PullRequestServiceClient pullRequestServiceClient;
    private final DateToISO8601Converter dateConverter;

    private final EventDataLayer eventDataLayer;
    private final UserDataLayer userDataLayer;
    private final Converter<RepositoryIssueEvent, User> eventUserConverter;
    private final Converter<RepositoryIssueEvent, Event> eventConverter;
    private final RateLimitResetTimerSubject rateLimitResetTimerSubject;

    private final RateLimitDelayTransformer<GithubComment> commentRateLimitDelayTransformer;

    public static CommentsServiceClient newInstance() {
        IssueService issueService = GithubIssueService.newInstance();
        PullRequestServiceClient pullRequestServiceClient = PullRequestServiceClient.newInstance();

        DateToISO8601Converter dateConverter = new DateToISO8601Converter();

        ConnectionManager connectionManager = ConnectionManagerContainer.getConnectionManager();
        EventDataLayer eventDataLayer = DbEventDataLayer.newInstance(connectionManager);
        UserDataLayer userDataLayer = DbUserDataLayer.newInstance(connectionManager);
        Converter<RepositoryIssueEvent, User> eventUserConverter = EventUserConverter.newInstance();
        Converter<RepositoryIssueEvent, Event> eventConverter = EventConverter.newInstance();

        RateLimitResetTimerSubject rateLimitResetTimerSubject = RateLimitResetTimerSubjectContainer.getInstance();
        RateLimitDelayTransformer<GithubComment> commentRateLimitDelayTransformer = RateLimitDelayTransformer.newInstance();

        return new CommentsServiceClient(issueService,
                                         pullRequestServiceClient,
                                         dateConverter,
                                         eventDataLayer,
                                         userDataLayer,
                                         eventUserConverter,
                                         eventConverter,
                                         rateLimitResetTimerSubject,
                                         commentRateLimitDelayTransformer);
    }

    private CommentsServiceClient(IssueService issueService,
                                  PullRequestServiceClient pullRequestServiceClient,
                                  DateToISO8601Converter dateConverter,
                                  EventDataLayer eventDataLayer,
                                  UserDataLayer userDataLayer,
                                  Converter<RepositoryIssueEvent, User> eventUserConverter,
                                  Converter<RepositoryIssueEvent, Event> eventConverter,
                                  RateLimitResetTimerSubject rateLimitResetTimerSubject,
                                  RateLimitDelayTransformer<GithubComment> commentRateLimitDelayTransformer) {

        this.issueService = issueService;
        this.pullRequestServiceClient = pullRequestServiceClient;
        this.dateConverter = dateConverter;
        this.eventDataLayer = eventDataLayer;
        this.userDataLayer = userDataLayer;
        this.eventUserConverter = eventUserConverter;
        this.eventConverter = eventConverter;
        this.rateLimitResetTimerSubject = rateLimitResetTimerSubject;
        this.commentRateLimitDelayTransformer = commentRateLimitDelayTransformer;
    }

    public Observable<RepositoryIssueEvent> retrieveCommentsFrom(RepositoryIssue repositoryIssue, Date since) {
        return Observable.merge(
                retrieveCommentsFromIssue(repositoryIssue, since),
                retrieveReviewCommentsFromPullRequest(repositoryIssue, since)
        )
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
        return pullRequestServiceClient
                .getPullRequestReviewCommentsFor(organisation, repository, issueNumber, since)
                .compose(RetryWhenTokenResets.newInstance(rateLimitResetTimerSubject));
    }

    private boolean isNotPullRequest(RepositoryIssue repositoryIssue) {
        return !repositoryIssue.isPullRequest();
    }

}
