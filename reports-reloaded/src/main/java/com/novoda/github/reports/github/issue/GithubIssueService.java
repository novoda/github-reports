package com.novoda.github.reports.github.issue;

import com.novoda.github.reports.github.network.GithubApiService;
import com.novoda.github.reports.github.network.GithubServiceFactory;
import com.novoda.github.reports.github.network.NextPageExtractor;

import java.util.List;
import java.util.Optional;

import retrofit2.Response;
import rx.Observable;

class GithubIssueService implements IssueService {

    private static final int DEFAULT_PER_PAGE_COUNT = 100;

    private final GithubApiService githubApiService;
    private final NextPageExtractor nextPageExtractor;

    public static IssueService newInstance() {
        GithubServiceFactory githubServiceFactory = GithubServiceFactory.newInstance();
        NextPageExtractor nextPageExtractor = new NextPageExtractor();
        return new GithubIssueService(githubServiceFactory.createService(), nextPageExtractor);
    }

    GithubIssueService(GithubApiService githubApiService, NextPageExtractor nextPageExtractor) {
        this.githubApiService = githubApiService;
        this.nextPageExtractor = nextPageExtractor;
    }

    @Override
    public Observable<Issue> getPagedIssuesFor(String organisation, String repository) {
        return getPagedIssuesFor(organisation, repository, 1)
                .flatMapIterable(Response::body);
    }

    private Observable<Response<List<Issue>>> getPagedIssuesFor(String organisation, String repository, Integer page) {
        return githubApiService
                .getIssuesResponseForPage(organisation, repository, page, DEFAULT_PER_PAGE_COUNT)
                .concatMap(response -> {
                    Optional<Integer> nextPage = nextPageExtractor.getNextPageFrom(response);
                    Observable<Response<List<Issue>>> observable = Observable.just(response);
                    if (nextPage.isPresent()) {
                        return observable.mergeWith(getPagedIssuesFor(organisation, repository, nextPage.get()));
                    }
                    return observable;
                });
    }
}
