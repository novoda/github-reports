package com.novoda.github.reports.batch.local.repository;

import com.novoda.github.reports.batch.local.retry.RateLimitResetTimerSubject;
import com.novoda.github.reports.batch.local.retry.RateLimitResetTimerSubjectContainer;
import com.novoda.github.reports.batch.local.retry.RetryWhenTokenResets;
import com.novoda.github.reports.data.RepoDataLayer;
import com.novoda.github.reports.data.db.ConnectionManager;
import com.novoda.github.reports.data.db.DbRepoDataLayer;
import com.novoda.github.reports.data.model.Repository;
import com.novoda.github.reports.service.network.GithubApiService;
import com.novoda.github.reports.service.network.GithubCachingServiceContainer;
import com.novoda.github.reports.service.network.PagedTransformer;
import com.novoda.github.reports.service.network.RateLimitDelayTransformer;
import com.novoda.github.reports.service.persistence.ConnectionManagerContainer;
import com.novoda.github.reports.service.persistence.PersistRepositoryTransformer;
import com.novoda.github.reports.service.persistence.converter.Converter;
import com.novoda.github.reports.service.persistence.converter.RepositoryConverter;
import com.novoda.github.reports.service.repository.GithubRepository;

import java.util.List;

import retrofit2.Response;
import rx.Observable;

public class RepositoriesServiceClient {

    private static final int DEFAULT_PER_PAGE_COUNT = 100;
    private static final int FIRST_PAGE = 1;

    private final GithubApiService apiService;
    private final RepoDataLayer repoDataLayer;
    private final Converter<GithubRepository, Repository> converter;

    private final RateLimitDelayTransformer<GithubRepository> rateLimitDelayTransformer;

    private final RateLimitResetTimerSubject rateLimitResetTimerSubject;

    public static RepositoriesServiceClient newInstance() {
        GithubApiService apiService = GithubCachingServiceContainer.getGithubService();
        ConnectionManager connectionManager = ConnectionManagerContainer.getConnectionManager();
        RepoDataLayer repoDataLayer = DbRepoDataLayer.newInstance(connectionManager);
        Converter<GithubRepository, Repository> converter = RepositoryConverter.newInstance();
        RateLimitResetTimerSubject rateLimitResetTimerSubject = RateLimitResetTimerSubjectContainer.getInstance();
        RateLimitDelayTransformer<GithubRepository> rateLimitDelayTransformer = RateLimitDelayTransformer.newInstance();
        return new RepositoriesServiceClient(apiService, repoDataLayer, converter, rateLimitResetTimerSubject, rateLimitDelayTransformer);
    }

    private RepositoriesServiceClient(GithubApiService apiService,
                                      RepoDataLayer repoDataLayer,
                                      Converter<GithubRepository, Repository> converter,
                                      RateLimitResetTimerSubject rateLimitResetTimerSubject,
                                      RateLimitDelayTransformer<GithubRepository> rateLimitDelayTransformer) {

        this.apiService = apiService;
        this.repoDataLayer = repoDataLayer;
        this.converter = converter;
        this.rateLimitResetTimerSubject = rateLimitResetTimerSubject;
        this.rateLimitDelayTransformer = rateLimitDelayTransformer;
    }

    public Observable<GithubRepository> retrieveRepositoriesFrom(String organisation) {
        return getPagedRepositoriesFor(organisation, FIRST_PAGE, DEFAULT_PER_PAGE_COUNT)
                .flatMapIterable(Response::body)
                .compose(RetryWhenTokenResets.newInstance(rateLimitResetTimerSubject))
                .compose(PersistRepositoryTransformer.newInstance(repoDataLayer, converter));
    }

    private Observable<Response<List<GithubRepository>>> getPagedRepositoriesFor(String organisation, int page, int pageCount) {
        return apiService.getRepositoriesResponseForPage(organisation, page, pageCount)
                .compose(rateLimitDelayTransformer)
                .compose(PagedTransformer.newInstance(nextPage -> getPagedRepositoriesFor(organisation, nextPage, pageCount)));
    }

}
