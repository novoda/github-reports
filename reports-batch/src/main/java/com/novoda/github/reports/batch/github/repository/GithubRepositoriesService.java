package com.novoda.github.reports.batch.github.repository;

import com.novoda.github.reports.batch.github.network.PagedTransformer;
import com.novoda.github.reports.batch.github.network.GithubApiService;
import com.novoda.github.reports.batch.github.network.GithubServiceFactory;

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
        return getPagedRepositoriesFor(organisation, 1, DEFAULT_PER_PAGE_COUNT)
                .flatMapIterable(Response::body);
    }

    private Observable<Response<List<Repository>>> getPagedRepositoriesFor(String org, Integer page, Integer pageCount) {
        return githubApiService
                .getRepositoriesResponseForPage(org, page, pageCount)
                .compose(PagedTransformer.newInstance(nextPage -> getPagedRepositoriesFor(org, nextPage, pageCount)));
    }

}
