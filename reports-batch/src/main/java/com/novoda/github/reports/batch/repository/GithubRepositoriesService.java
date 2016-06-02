package com.novoda.github.reports.batch.repository;

import com.novoda.github.reports.batch.network.GithubApiService;
import com.novoda.github.reports.batch.network.GithubServiceFactory;
import com.novoda.github.reports.batch.network.PagedTransformer;
import com.novoda.github.reports.batch.network.RepositoryRateLimitDelayTransformer;

import java.util.List;

import retrofit2.Response;
import rx.Observable;

class GithubRepositoriesService implements RepositoryService {

    private static final int DEFAULT_PER_PAGE_COUNT = 100;
    private static final int FIRST_PAGE = 1;

    private final GithubApiService githubApiService;
    private final RepositoryRateLimitDelayTransformer rateLimitDelayTransformer;

    static GithubRepositoriesService newInstance() {
        GithubServiceFactory githubServiceFactory = GithubServiceFactory.newInstance();
        RepositoryRateLimitDelayTransformer rateLimitDelayTransformer = RepositoryRateLimitDelayTransformer.newInstance();
        return new GithubRepositoriesService(githubServiceFactory.createService(), rateLimitDelayTransformer);
    }

    private GithubRepositoriesService(GithubApiService githubApiService, RepositoryRateLimitDelayTransformer rateLimitDelayTransformer) {
        this.githubApiService = githubApiService;
        this.rateLimitDelayTransformer = rateLimitDelayTransformer;
    }

    @Override
    public Observable<Repository> getPagedRepositoriesFor(String organisation) {
        return getPagedRepositoriesFor(organisation, FIRST_PAGE, DEFAULT_PER_PAGE_COUNT)
                .flatMapIterable(Response::body);
    }

    private Observable<Response<List<Repository>>> getPagedRepositoriesFor(String organisation, Integer page, Integer pageCount) {
        return githubApiService.getRepositoriesResponseForPage(organisation, page, pageCount)
                .compose(rateLimitDelayTransformer)
                .compose(PagedTransformer.newInstance(nextPage -> getPagedRepositoriesFor(organisation, nextPage, pageCount)));
    }
}
