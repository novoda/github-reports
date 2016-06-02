package com.novoda.github.reports.batch.repository;

import com.novoda.github.reports.batch.persistence.ConnectionManagerContainer;
import com.novoda.github.reports.batch.persistence.Converter;
import com.novoda.github.reports.batch.persistence.PersistRepositoryTransformer;
import com.novoda.github.reports.batch.persistence.RepositoryConverter;
import com.novoda.github.reports.data.RepoDataLayer;
import com.novoda.github.reports.data.db.ConnectionManager;
import com.novoda.github.reports.data.db.DbRepoDataLayer;

import java.util.Date;

import org.joda.time.DateTime;

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
                .compose(PersistRepositoryTransformer.newInstance(repoDataLayer, converter))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.immediate());
    }

    public Observable<Repository> getRepositoriesSince(String organisation, Date since) {
        DateTime dateTime = new DateTime(since.getTime());
        return repositoryService.getPagedRepositoriesSince(organisation, dateTime.toString())
                //.compose(PersistRepositoryTransformer.newInstance(repoDataLayer, converter))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.immediate());
    }

}
