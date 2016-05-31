package com.novoda.github.reports.batch.repository;

import com.novoda.github.reports.batch.network.GithubApiService;
import com.novoda.github.reports.batch.network.GithubServiceFactory;
import com.novoda.github.reports.batch.network.PagedTransformer;
import com.novoda.github.reports.batch.network.RateLimitRemainingCounterContainer;
import com.novoda.github.reports.batch.network.RateLimitRemainingResetRepositoryContainer;

import java.util.List;
import java.util.concurrent.TimeUnit;

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
        return delayIfNeededGetPagedRepositoriesFor(org, page, pageCount)
                .compose(PagedTransformer.newInstance(nextPage -> getPagedRepositoriesFor(org, nextPage, pageCount)));
    }

    private Observable<Response<List<Repository>>> delayIfNeededGetPagedRepositoriesFor(String organisation, Integer page, Integer pageCount) {
        int numberOfRemainingRequests = RateLimitRemainingCounterContainer.getInstance().get();
        if (numberOfRemainingRequests == 0) {
            long resetTimestamp = RateLimitRemainingResetRepositoryContainer.getInstance().get();
            long delay = resetTimestamp - System.currentTimeMillis();
            return githubApiService
                    .getRepositoriesResponseForPage(organisation, page, pageCount)
                    .delaySubscription(delay, TimeUnit.MILLISECONDS);
        } else {
            return githubApiService.getRepositoriesResponseForPage(organisation, page, pageCount);
        }
    }

}
