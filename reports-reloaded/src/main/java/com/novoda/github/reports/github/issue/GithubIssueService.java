package com.novoda.github.reports.github.issue;

import com.novoda.github.reports.github.PagedTransformer;
import com.novoda.github.reports.github.network.GithubApiService;
import com.novoda.github.reports.github.network.GithubServiceFactory;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

import retrofit2.Response;
import rx.Observable;

class GithubIssueService implements IssueService {

    private static final int DEFAULT_PER_PAGE_COUNT = 100;
    private static final Issue.State DEFAULT_STATE = Issue.State.ALL;
    private static final String NO_SINCE_DATE = null;

    private final GithubApiService githubApiService;

    public static IssueService newInstance() {
        GithubServiceFactory githubServiceFactory = GithubServiceFactory.newInstance();
        return new GithubIssueService(githubServiceFactory.createService());
    }

    private GithubIssueService(GithubApiService githubApiService) {
        this.githubApiService = githubApiService;
    }

    @Override
    public Observable<Issue> getPagedIssuesFor(String organisation, String repository) {
        return getPagedIssuesFor(organisation, repository, NO_SINCE_DATE, 1)
                .flatMapIterable(Response::body);
    }

    private Observable<Response<List<Issue>>> getPagedIssuesFor(String organisation, String repository, String since, Integer page) {
        return githubApiService
                .getIssuesResponseForPage(organisation, repository, DEFAULT_STATE, since, page, DEFAULT_PER_PAGE_COUNT)
                .compose(PagedTransformer.newInstance(nextPage -> getPagedIssuesFor(organisation, repository, since, nextPage)));
    }

    @Override
    public Observable<Issue> getPagedIssuesFor(String organisation, String repository, Date since) {
        String date = new DateTime(since.getTime()).toString();
        return getPagedIssuesFor(organisation, repository, date, 1)
                .flatMapIterable(Response::body);
    }
}
