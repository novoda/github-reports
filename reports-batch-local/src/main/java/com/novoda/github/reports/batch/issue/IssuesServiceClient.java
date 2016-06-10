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
import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.service.issue.GithubIssueService;
import com.novoda.github.reports.service.issue.IssueService;
import com.novoda.github.reports.service.issue.RepositoryIssue;
import com.novoda.github.reports.service.network.DateToISO8601Converter;
import com.novoda.github.reports.service.network.PagedTransformer;
import com.novoda.github.reports.service.network.RateLimitDelayTransformer;
import com.novoda.github.reports.service.persistence.ConnectionManagerContainer;
import com.novoda.github.reports.service.persistence.PersistIssueTransformer;
import com.novoda.github.reports.service.persistence.PersistUserTransformer;
import com.novoda.github.reports.service.persistence.converter.Converter;
import com.novoda.github.reports.service.persistence.converter.IssueConverter;
import com.novoda.github.reports.service.persistence.converter.UserConverter;
import com.novoda.github.reports.service.repository.GithubRepository;

import java.util.Date;
import java.util.List;

import retrofit2.Response;
import rx.Observable;

public class IssuesServiceClient {

    private static final int DEFAULT_PER_PAGE_COUNT = 100;
    private static final int FIRST_PAGE = 1;
    private static final GithubIssue.State DEFAULT_STATE = GithubIssue.State.ALL;

    private final IssueService issueService;
    private final DateToISO8601Converter dateConverter;

    private final RateLimitDelayTransformer<GithubIssue> issueRateLimitDelayTransformer;
    private final RateLimitResetTimerSubject rateLimitResetTimerSubject;

    private final EventDataLayer eventDataLayer;
    private final Converter<RepositoryIssue, Event> issueConverter;
    private final UserDataLayer userDataLayer;
    private final Converter<RepositoryIssue, User> userConverter;

    public static IssuesServiceClient newInstance() {
        IssueService issueService = GithubIssueService.newInstance();

        DateToISO8601Converter dateConverter = new DateToISO8601Converter();
        RateLimitDelayTransformer<GithubIssue> issueRateLimitDelayTransformer = RateLimitDelayTransformer.newInstance();

        ConnectionManager connectionManager = ConnectionManagerContainer.getConnectionManager();
        EventDataLayer eventDataLayer = DbEventDataLayer.newInstance(connectionManager);
        Converter<RepositoryIssue, Event> issueConverter = IssueConverter.newInstance();
        UserDataLayer userDataLayer = DbUserDataLayer.newInstance(connectionManager);
        Converter<RepositoryIssue, User> userConverter = UserConverter.newInstance();

        RateLimitResetTimerSubject rateLimitResetTimerSubject = RateLimitResetTimerSubjectContainer.getInstance();

        return new IssuesServiceClient(issueService,
                                       dateConverter,
                                       eventDataLayer,
                                       userDataLayer,
                                       userConverter,
                                       issueConverter,
                                       rateLimitResetTimerSubject,
                                       issueRateLimitDelayTransformer
        );
    }

    private IssuesServiceClient(IssueService issueService,
                                DateToISO8601Converter dateConverter,
                                EventDataLayer eventDataLayer,
                                UserDataLayer userDataLayer,
                                Converter<RepositoryIssue, User> userConverter,
                                Converter<RepositoryIssue, Event> issueConverter, RateLimitResetTimerSubject rateLimitResetTimerSubject, RateLimitDelayTransformer<GithubIssue> issueRateLimitDelayTransformer) {

        this.issueService = issueService;
        this.dateConverter = dateConverter;
        this.issueRateLimitDelayTransformer = issueRateLimitDelayTransformer;
        this.rateLimitResetTimerSubject = rateLimitResetTimerSubject;
        this.eventDataLayer = eventDataLayer;
        this.issueConverter = issueConverter;
        this.userDataLayer = userDataLayer;
        this.userConverter = userConverter;
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

}
