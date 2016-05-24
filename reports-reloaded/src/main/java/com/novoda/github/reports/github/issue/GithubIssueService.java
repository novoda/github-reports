package com.novoda.github.reports.github.issue;

import com.novoda.github.reports.github.network.GithubApiService;
import com.novoda.github.reports.github.network.GithubServiceFactory;
import com.novoda.github.reports.github.network.NextPageExtractor;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.joda.time.DateTime;

import retrofit2.Response;
import rx.Observable;

class GithubIssueService implements IssueService {

    private static final int DEFAULT_PER_PAGE_COUNT = 100;
    private static final State DEFAULT_STATE = State.ALL;
    private static final String NO_SINCE_DATE = null;

    private final GithubApiService githubApiService;
    private final NextPageExtractor nextPageExtractor;

    public static IssueService newInstance() {
        GithubServiceFactory githubServiceFactory = GithubServiceFactory.newInstance();
        NextPageExtractor nextPageExtractor = new NextPageExtractor();
        return new GithubIssueService(githubServiceFactory.createService(), nextPageExtractor);
    }

    private GithubIssueService(GithubApiService githubApiService, NextPageExtractor nextPageExtractor) {
        this.githubApiService = githubApiService;
        this.nextPageExtractor = nextPageExtractor;
    }

    @Override
    public Observable<Issue> getPagedIssuesFor(String organisation, String repository) {
        return getPagedIssuesFor(organisation, repository, NO_SINCE_DATE, 1)
                .flatMapIterable(Response::body);
    }

    private Observable<Response<List<Issue>>> getPagedIssuesFor(String organisation, String repository, String since, Integer page) {
        return githubApiService
                .getIssuesResponseForPage(organisation, repository, DEFAULT_STATE, since, page, DEFAULT_PER_PAGE_COUNT)
                .concatMap(response -> {
                    Optional<Integer> nextPage = nextPageExtractor.getNextPageFrom(response);
                    Observable<Response<List<Issue>>> observable = Observable.just(response);
                    if (nextPage.isPresent()) {
                        return observable.mergeWith(getPagedIssuesFor(organisation, repository, since, nextPage.get()));
                    }
                    return observable;
                });
    }

    @Override
    public Observable<Issue> getPagedIssuesFor(String organisation, String repository, Date since) {
        String date = new DateTime(since.getTime()).toString();
        return getPagedIssuesFor(organisation, repository, date, 1)
                .flatMapIterable(Response::body);
    }
}
