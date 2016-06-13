package com.novoda.github.reports.batch.issue;

import com.novoda.github.reports.batch.retry.RateLimitResetTimerSubject;
import com.novoda.github.reports.batch.retry.RateLimitResetTimerSubjectContainer;
import com.novoda.github.reports.batch.retry.RetryWhenTokenResets;
import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.service.issue.GithubIssueService;
import com.novoda.github.reports.service.issue.IssueService;
import com.novoda.github.reports.service.issue.RepositoryIssue;
import com.novoda.github.reports.service.network.DateToISO8601Converter;
import com.novoda.github.reports.service.network.PagedTransformer;
import com.novoda.github.reports.service.network.RateLimitDelayTransformer;
import com.novoda.github.reports.service.persistence.IssuePersister;
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
    private final IssuePersister issuePersister;

    private final RateLimitDelayTransformer<GithubIssue> issueRateLimitDelayTransformer;
    private final RateLimitResetTimerSubject rateLimitResetTimerSubject;

    public static IssuesServiceClient newInstance() {
        IssueService issueService = GithubIssueService.newCachingInstance();
        DateToISO8601Converter dateConverter = new DateToISO8601Converter();
        IssuePersister issuePersister = IssuePersister.newInstance();
        RateLimitDelayTransformer<GithubIssue> issueRateLimitDelayTransformer = RateLimitDelayTransformer.newInstance();
        RateLimitResetTimerSubject rateLimitResetTimerSubject = RateLimitResetTimerSubjectContainer.getInstance();
        return new IssuesServiceClient(issueService, dateConverter, issuePersister, rateLimitResetTimerSubject, issueRateLimitDelayTransformer);
    }

    private IssuesServiceClient(IssueService issueService,
                                DateToISO8601Converter dateConverter,
                                IssuePersister issuePersister,
                                RateLimitResetTimerSubject rateLimitResetTimerSubject,
                                RateLimitDelayTransformer<GithubIssue> issueRateLimitDelayTransformer) {

        this.issueService = issueService;
        this.dateConverter = dateConverter;
        this.issuePersister = issuePersister;
        this.issueRateLimitDelayTransformer = issueRateLimitDelayTransformer;
        this.rateLimitResetTimerSubject = rateLimitResetTimerSubject;
    }

    public Observable<RepositoryIssue> retrieveIssuesFrom(GithubRepository repository, Date since) {
        return getPagedIssuesFor(repository.getOwnerUsername(), repository.getName(), since, FIRST_PAGE, DEFAULT_PER_PAGE_COUNT)
                .flatMapIterable(Response::body)
                .compose(RetryWhenTokenResets.newInstance(rateLimitResetTimerSubject))
                .map(issue -> RepositoryIssue.newInstance(repository, issue))
                .compose(issuePersister);
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
