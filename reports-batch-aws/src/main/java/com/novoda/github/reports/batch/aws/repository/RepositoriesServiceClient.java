package com.novoda.github.reports.batch.aws.repository;

import com.novoda.github.reports.data.RepoDataLayer;
import com.novoda.github.reports.data.db.ConnectionManager;
import com.novoda.github.reports.data.db.DbRepoDataLayer;
import com.novoda.github.reports.data.model.Repository;
import com.novoda.github.reports.service.persistence.ConnectionManagerContainer;
import com.novoda.github.reports.service.persistence.PersistRepositoryTransformer;
import com.novoda.github.reports.service.persistence.converter.Converter;
import com.novoda.github.reports.service.persistence.converter.RepositoryConverter;
import com.novoda.github.reports.service.repository.GithubRepositoryService;
import com.novoda.github.reports.service.repository.GithubRepository;
import com.novoda.github.reports.service.repository.RepositoryService;

import retrofit2.Response;
import rx.Observable;

public class RepositoriesServiceClient {

    private static final int DEFAULT_PER_PAGE_COUNT = 100;

    private final RepositoryService repositoryService;
    private final PersistRepositoryTransformer persistRepositoryTransformer;

    public static RepositoriesServiceClient newInstance() {
        RepositoryService repositoriesService = GithubRepositoryService.newInstance();
        ConnectionManager connectionManager = ConnectionManagerContainer.getConnectionManager();
        RepoDataLayer repoDataLayer = DbRepoDataLayer.newInstance(connectionManager);
        Converter<GithubRepository, Repository> converter = RepositoryConverter.newInstance();
        PersistRepositoryTransformer persistRepositoryTransformer = PersistRepositoryTransformer.newInstance(repoDataLayer, converter);
        return new RepositoriesServiceClient(repositoriesService, persistRepositoryTransformer);
    }

    private RepositoriesServiceClient(RepositoryService repositoryService, PersistRepositoryTransformer persistRepositoryTransformer) {
        this.repositoryService = repositoryService;
        this.persistRepositoryTransformer = persistRepositoryTransformer;
    }

    public Observable<GithubRepository> getRepositoriesFor(String organisation, int page) {
        return repositoryService.getRepositoriesFor(organisation, page, DEFAULT_PER_PAGE_COUNT)
                .flatMapIterable(Response::body)
                .compose(persistRepositoryTransformer);

    }

}
