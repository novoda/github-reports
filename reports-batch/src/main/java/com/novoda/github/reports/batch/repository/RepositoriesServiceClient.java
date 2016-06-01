package com.novoda.github.reports.batch.repository;

import com.novoda.github.reports.batch.persistence.ConnectionManagerContainer;
import com.novoda.github.reports.batch.persistence.Converter;
import com.novoda.github.reports.batch.persistence.PersistRepositoryOperator;
import com.novoda.github.reports.batch.persistence.RepositoryConverter;
import com.novoda.github.reports.data.RepoDataLayer;
import com.novoda.github.reports.data.db.ConnectionManager;
import com.novoda.github.reports.data.db.DbRepoDataLayer;

import rx.Observable;
import rx.schedulers.Schedulers;

public class RepositoriesServiceClient {

    private final RepositoryService repositoryService;
    private final RepoDataLayer repoDataLayer;
    private final Converter<Repository, com.novoda.github.reports.data.model.Repository> converter;

    public static RepositoriesServiceClient newInstance() {
        GithubRepositoriesService repositoriesService = GithubRepositoriesService.newInstance();
        ConnectionManager connectionManager = ConnectionManagerContainer.getConnectionManager();
        RepoDataLayer repoDataLayer = DbRepoDataLayer.newInstance(connectionManager);
        Converter<Repository, com.novoda.github.reports.data.model.Repository> converter = RepositoryConverter.newInstance();
        return new RepositoriesServiceClient(repositoriesService, repoDataLayer, converter);
    }

    private RepositoriesServiceClient(GithubRepositoriesService repositoryService,
                                      RepoDataLayer repoDataLayer,
                                      Converter<Repository, com.novoda.github.reports.data.model.Repository> converter) {
        this.repositoryService = repositoryService;
        this.repoDataLayer = repoDataLayer;
        this.converter = converter;
    }

    public Observable<Repository> retrieveRepositoriesFrom(String organisation) {
        return repositoryService.getPagedRepositoriesFor(organisation)
                .lift(PersistRepositoryOperator.newInstance(repoDataLayer, converter))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.immediate());
    }

}
