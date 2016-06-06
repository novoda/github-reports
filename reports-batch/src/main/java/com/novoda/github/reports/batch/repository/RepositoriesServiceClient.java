package com.novoda.github.reports.batch.repository;

import com.novoda.github.reports.batch.network.RateLimitRemainingResetRepositoryContainer;
import com.novoda.github.reports.batch.network.RateLimitResetRepository;
import com.novoda.github.reports.batch.persistence.ConnectionManagerContainer;
import com.novoda.github.reports.batch.persistence.converter.Converter;
import com.novoda.github.reports.batch.persistence.PersistRepositoryTransformer;
import com.novoda.github.reports.batch.persistence.converter.RepositoryConverter;
import com.novoda.github.reports.batch.retry.RateLimitResetTimerSubject;
import com.novoda.github.reports.batch.retry.RateLimitResetTimerSubjectContainer;
import com.novoda.github.reports.batch.retry.RetryWhenTokenResets;
import com.novoda.github.reports.data.RepoDataLayer;
import com.novoda.github.reports.data.db.ConnectionManager;
import com.novoda.github.reports.data.db.DbRepoDataLayer;

import rx.Observable;
import rx.schedulers.Schedulers;

public class RepositoriesServiceClient {

    private final RepositoryService repositoryService;
    private final RepoDataLayer repoDataLayer;
    private final Converter<Repository, com.novoda.github.reports.data.model.Repository> converter;

    private final RateLimitResetRepository rateLimitResetRepository;
    private final RateLimitResetTimerSubject rateLimitResetTimerSubject;

    public static RepositoriesServiceClient newInstance() {
        GithubRepositoriesService repositoriesService = GithubRepositoriesService.newInstance();
        ConnectionManager connectionManager = ConnectionManagerContainer.getConnectionManager();
        RepoDataLayer repoDataLayer = DbRepoDataLayer.newInstance(connectionManager);
        Converter<Repository, com.novoda.github.reports.data.model.Repository> converter = RepositoryConverter.newInstance();
        RateLimitResetRepository rateLimitResetRepository = RateLimitRemainingResetRepositoryContainer.getInstance();
        RateLimitResetTimerSubject rateLimitResetTimerSubject = RateLimitResetTimerSubjectContainer.getInstance();
        return new RepositoriesServiceClient(repositoriesService, repoDataLayer, converter, rateLimitResetRepository, rateLimitResetTimerSubject);
    }

    private RepositoriesServiceClient(GithubRepositoriesService repositoryService,
                                      RepoDataLayer repoDataLayer,
                                      Converter<Repository, com.novoda.github.reports.data.model.Repository> converter,
                                      RateLimitResetRepository rateLimitResetRepository,
                                      RateLimitResetTimerSubject rateLimitResetTimerSubject) {
        this.repositoryService = repositoryService;
        this.repoDataLayer = repoDataLayer;
        this.converter = converter;
        this.rateLimitResetRepository = rateLimitResetRepository;
        this.rateLimitResetTimerSubject = rateLimitResetTimerSubject;
    }

    public Observable<Repository> retrieveRepositoriesFrom(String organisation) {
        return repositoryService.getPagedRepositoriesFor(organisation)
                .compose(RetryWhenTokenResets.newInstance(rateLimitResetTimerSubject, rateLimitResetRepository))
                .compose(PersistRepositoryTransformer.newInstance(repoDataLayer, converter))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.immediate());
    }

}
