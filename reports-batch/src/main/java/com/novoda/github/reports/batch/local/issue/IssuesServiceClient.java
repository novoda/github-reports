package com.novoda.github.reports.batch.local.issue;

import com.novoda.github.reports.batch.local.retry.RateLimitResetTimerSubject;
import com.novoda.github.reports.batch.local.retry.RateLimitResetTimerSubjectContainer;
import com.novoda.github.reports.batch.local.retry.RetryWhenTokenResets;
import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.service.issue.RepositoryIssue;
import com.novoda.github.reports.service.network.DateToISO8601Converter;
import com.novoda.github.reports.service.network.GithubApiService;
import com.novoda.github.reports.service.network.GithubCachingServiceContainer;
import com.novoda.github.reports.service.network.PagedTransformer;
import com.novoda.github.reports.service.network.RateLimitDelayTransformer;
import com.novoda.github.reports.service.persistence.RepositoryIssuePersistTransformer;
import com.novoda.github.reports.service.repository.GithubRepository;

import java.util.Date;
import java.util.List;

import retrofit2.Response;
import rx.Observable;

public class IssuesServiceClient {

    private static final int DEFAULT_PER_PAGE_COUNT = 100;
    private static final int FIRST_PAGE = 1;
    private static final GithubIssue.State DEFAULT_STATE = GithubIssue.State.ALL;

    private final GithubApiService apiService;
    private final DateToISO8601Converter dateConverter;
    private final RepositoryIssuePersistTransformer repositoryIssuePersistTransformer;

    private final RateLimitDelayTransformer<GithubIssue> issueRateLimitDelayTransformer;
    private final RateLimitResetTimerSubject rateLimitResetTimerSubject;

    public static IssuesServiceClient newInstance() {
        GithubApiService apiService = GithubCachingServiceContainer.getGithubService();
        DateToISO8601Converter dateConverter = new DateToISO8601Converter();
        RepositoryIssuePersistTransformer repositoryIssuePersistTransformer = RepositoryIssuePersistTransformer.newInstance();
        RateLimitDelayTransformer<GithubIssue> issueRateLimitDelayTransformer = RateLimitDelayTransformer.newInstance();
        RateLimitResetTimerSubject rateLimitResetTimerSubject = RateLimitResetTimerSubjectContainer.getInstance();
        return new IssuesServiceClient(apiService,
                                       dateConverter,
                                       repositoryIssuePersistTransformer,
                                       rateLimitResetTimerSubject,
                                       issueRateLimitDelayTransformer);
    }

    private IssuesServiceClient(GithubApiService apiService,
                                DateToISO8601Converter dateConverter,
                                RepositoryIssuePersistTransformer repositoryIssuePersistTransformer,
                                RateLimitResetTimerSubject rateLimitResetTimerSubject,
                                RateLimitDelayTransformer<GithubIssue> issueRateLimitDelayTransformer) {

        this.apiService = apiService;
        this.dateConverter = dateConverter;
        this.repositoryIssuePersistTransformer = repositoryIssuePersistTransformer;
        this.issueRateLimitDelayTransformer = issueRateLimitDelayTransformer;
        this.rateLimitResetTimerSubject = rateLimitResetTimerSubject;
    }

    public Observable<RepositoryIssue> retrieveIssuesFrom(GithubRepository repository, Date since) {
        return getPagedIssuesFor(repository.getOwnerUsername(), repository.getName(), since, FIRST_PAGE, DEFAULT_PER_PAGE_COUNT)
                .flatMapIterable(Response::body)
                .compose(RetryWhenTokenResets.newInstance(rateLimitResetTimerSubject))
                .map(issue -> new RepositoryIssue(repository, issue))
                .compose(repositoryIssuePersistTransformer);
    }

    private Observable<Response<List<GithubIssue>>> getPagedIssuesFor(String organisation,
                                                                      String repository,
                                                                      Date since,
                                                                      int page,
                                                                      int pageCount) {

        String date = dateConverter.toISO8601NoMillisOrNull(since);
        return apiService.getIssuesResponseForPage(organisation, repository, DEFAULT_STATE, date, page, pageCount)
                .compose(issueRateLimitDelayTransformer)
                .compose(PagedTransformer.newInstance(nextPage -> getPagedIssuesFor(organisation, repository, since, nextPage, pageCount)));
    }

}
