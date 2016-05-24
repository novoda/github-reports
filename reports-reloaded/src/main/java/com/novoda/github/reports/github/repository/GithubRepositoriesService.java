package com.novoda.github.reports.github.repository;

import com.novoda.github.reports.github.network.GithubApiService;
import com.novoda.github.reports.github.network.GithubServiceFactory;
import com.novoda.github.reports.github.network.NextPageExtractor;

import java.util.List;
import java.util.Optional;

import retrofit2.Response;
import rx.Observable;

class GithubRepositoriesService implements RepositoryService {

    private final GithubApiService githubApiService;
    private final NextPageExtractor nextPageExtractor;

    static GithubRepositoriesService newInstance() {
        GithubServiceFactory githubServiceFactory = GithubServiceFactory.newInstance();
        NextPageExtractor nextPageExtractor = new NextPageExtractor();
        return new GithubRepositoriesService(githubServiceFactory.createService(), nextPageExtractor);
    }

    private GithubRepositoriesService(GithubApiService githubApiService, NextPageExtractor nextPageExtractor) {
        this.githubApiService = githubApiService;
        this.nextPageExtractor = nextPageExtractor;
    }

    @Override
    public Observable<Repository> getPagedRepositoriesFor(String organisation) {
        return getPagedRepositoriesFor(organisation, 1)
                .flatMapIterable(Response::body);
    }

    private Observable<Response<List<Repository>>> getPagedRepositoriesFor(String org, Integer page) {
        return githubApiService
                .getRepositoriesResponseForPage(org, page)
                .concatMap(response -> {
                    Optional<Integer> nextPage = nextPageExtractor.getNextPageFrom(response);
                    Observable<Response<List<Repository>>> observable = Observable.just(response);
                    if (nextPage.isPresent()) {
                        return observable.mergeWith(getPagedRepositoriesFor(org, nextPage.get()));
                    }
                    return observable;
                });
    }

}
