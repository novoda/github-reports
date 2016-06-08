package com.novoda.github.reports.service.repository;

import com.novoda.github.reports.service.network.GithubApiService;
import com.novoda.github.reports.service.network.GithubServiceContainer;
import com.novoda.github.reports.service.network.PagedTransformer;
import com.novoda.github.reports.service.network.RateLimitDelayTransformer;

import java.util.List;

import retrofit2.Response;
import rx.Observable;

public class GithubRepositoriesService implements RepositoryService {

    private static final int DEFAULT_PER_PAGE_COUNT = 100;
    private static final int FIRST_PAGE = 1;

    private final GithubApiService githubApiService;
    private final RateLimitDelayTransformer<GithubRepository> rateLimitDelayTransformer;

    public static GithubRepositoriesService newInstance() {
        GithubApiService githubApiService = GithubServiceContainer.getGithubService();
        RateLimitDelayTransformer<GithubRepository> rateLimitDelayTransformer = RateLimitDelayTransformer.newInstance();
        return new GithubRepositoriesService(githubApiService, rateLimitDelayTransformer);
    }

    private GithubRepositoriesService(GithubApiService githubApiService, RateLimitDelayTransformer<GithubRepository> rateLimitDelayTransformer) {
        this.githubApiService = githubApiService;
        this.rateLimitDelayTransformer = rateLimitDelayTransformer;
    }

    @Override
    public Observable<GithubRepository> getRepositoriesFor(String organisation) {
        return getPagedRepositoriesFor(organisation, FIRST_PAGE, DEFAULT_PER_PAGE_COUNT)
                .flatMapIterable(Response::body);
    }

    private Observable<Response<List<GithubRepository>>> getPagedRepositoriesFor(String organisation, Integer page, Integer pageCount) {
        return githubApiService.getRepositoriesResponseForPage(organisation, page, pageCount)
                .compose(rateLimitDelayTransformer)
                .compose(PagedTransformer.newInstance(nextPage -> getPagedRepositoriesFor(organisation, nextPage, pageCount)));
    }
}
