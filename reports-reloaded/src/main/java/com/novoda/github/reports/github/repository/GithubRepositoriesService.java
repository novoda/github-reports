package com.novoda.github.reports.github.repository;

import com.novoda.github.reports.github.PagedTransformer;
import com.novoda.github.reports.github.network.GithubApiService;
import com.novoda.github.reports.github.network.GithubServiceFactory;

import java.util.List;

import retrofit2.Response;
import rx.Observable;

class GithubRepositoriesService implements RepositoryService {

    private static final int DEFAULT_PER_PAGE_COUNT = 100;

    private final GithubApiService githubApiService;

    static GithubRepositoriesService newInstance() {
        GithubServiceFactory githubServiceFactory = GithubServiceFactory.newInstance();
        return new GithubRepositoriesService(githubServiceFactory.createService());
    }

    private GithubRepositoriesService(GithubApiService githubApiService) {
        this.githubApiService = githubApiService;
    }

    @Override
    public Observable<Repository> getPagedRepositoriesFor(String organisation) {
        return getPagedRepositoriesFor(organisation, 1)
                .flatMapIterable(Response::body);
    }

    private Observable<Response<List<Repository>>> getPagedRepositoriesFor(String org, Integer page) {
        return githubApiService
                .getRepositoriesResponseForPage(org, page, DEFAULT_PER_PAGE_COUNT)
                .compose(PagedTransformer.newInstance(nextPage -> getPagedRepositoriesFor(org, nextPage)));
    }

}
