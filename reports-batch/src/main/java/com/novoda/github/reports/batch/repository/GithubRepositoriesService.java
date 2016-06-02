package com.novoda.github.reports.batch.repository;

import com.novoda.github.reports.batch.network.GithubApiService;
import com.novoda.github.reports.batch.network.GithubServiceFactory;
import com.novoda.github.reports.batch.network.PagedTransformer;
import com.novoda.github.reports.batch.network.RateLimitDelayTransformer;

import java.util.List;

import retrofit2.Response;
import rx.Observable;

class GithubRepositoriesService implements RepositoryService {

    private static final int DEFAULT_PER_PAGE_COUNT = 100;
    private static final int FIRST_PAGE = 1;
    private static final String NO_SINCE_DATE = null;

    private final GithubApiService githubApiService;
    private final RateLimitDelayTransformer<Repository> rateLimitDelayTransformer;

    static GithubRepositoriesService newInstance() {
        GithubServiceFactory githubServiceFactory = GithubServiceFactory.newInstance();
        RateLimitDelayTransformer<Repository> rateLimitDelayTransformer = RateLimitDelayTransformer.newInstance();
        return new GithubRepositoriesService(githubServiceFactory.createService(), rateLimitDelayTransformer);
    }

    private GithubRepositoriesService(GithubApiService githubApiService, RateLimitDelayTransformer<Repository> rateLimitDelayTransformer) {
        this.githubApiService = githubApiService;
        this.rateLimitDelayTransformer = rateLimitDelayTransformer;
    }

    @Override
    public Observable<Repository> getPagedRepositoriesFor(String organisation) {
        return getPagedRepositoriesFor(organisation, FIRST_PAGE, DEFAULT_PER_PAGE_COUNT)
                .flatMapIterable(Response::body);
    }

    private Observable<Response<List<Repository>>> getPagedRepositoriesFor(String organisation, Integer page, Integer pageCount) {
        return githubApiService.getRepositoriesResponseForPage(organisation, NO_SINCE_DATE, page, pageCount)
                .compose(rateLimitDelayTransformer)
                .compose(PagedTransformer.newInstance(nextPage -> getPagedRepositoriesFor(organisation, nextPage, pageCount)));
    }

    public Observable<Repository> getPagedRepositoriesSince(String organisation, String since) {
        return getPagedRepositories(organisation, since, FIRST_PAGE, DEFAULT_PER_PAGE_COUNT)
                .flatMapIterable(Response::body);
    }

    private Observable<Response<List<Repository>>> getPagedRepositories(String organisation, String since, Integer page, Integer pageCount) {
        return githubApiService.getRepositoriesResponseForPage(organisation, since, page, pageCount)
                .compose(rateLimitDelayTransformer)
                .compose(PagedTransformer.newInstance(nextPage -> getPagedRepositories(organisation, since, nextPage, pageCount)));
    }
}
