package com.novoda.github.reports.batch.aws.repository;

import com.novoda.github.reports.batch.aws.persistence.PersistOperator;
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

import java.util.List;

import retrofit2.Response;
import rx.Observable;

public class RepositoriesServiceClient {

    private static final int DEFAULT_PER_PAGE_COUNT = 100;

    private final RepositoryService repositoryService;
    private final PersistRepositoryTransformer persistRepositoryTransformer;

    private final RepoDataLayer repoDataLayer;
    private final Converter<GithubRepository, Repository> converter;

    public static RepositoriesServiceClient newInstance() {
        RepositoryService repositoriesService = GithubRepositoryService.newInstance();
        ConnectionManager connectionManager = ConnectionManagerContainer.getConnectionManager();
        RepoDataLayer repoDataLayer = DbRepoDataLayer.newInstance(connectionManager);
        Converter<GithubRepository, Repository> converter = RepositoryConverter.newInstance();
        PersistRepositoryTransformer persistRepositoryTransformer = PersistRepositoryTransformer.newInstance(repoDataLayer, converter);
        return new RepositoriesServiceClient(repositoriesService, persistRepositoryTransformer, repoDataLayer, converter);
    }

    private RepositoriesServiceClient(RepositoryService repositoryService,
                                      PersistRepositoryTransformer persistRepositoryTransformer,
                                      RepoDataLayer repoDataLayer,
                                      Converter<GithubRepository, Repository> converter) {

        this.repositoryService = repositoryService;
        this.persistRepositoryTransformer = persistRepositoryTransformer;
        this.repoDataLayer = repoDataLayer;
        this.converter = converter;
    }

    public Observable<GithubRepository> getRepositoriesFor(String organisation, int page) {
        return repositoryService.getRepositoriesFor(organisation, page, DEFAULT_PER_PAGE_COUNT)
                .flatMapIterable(Response::body)
                .compose(persistRepositoryTransformer);

    }

    public Observable<Response<List<GithubRepository>>> getRepositoriesResponseFor(String organisation, int page) {
        return repositoryService.getRepositoriesFor(organisation, page, DEFAULT_PER_PAGE_COUNT)
                .lift(new PersistOperator<>(repoDataLayer, converter)); // no need to buffer 'cause we already have a list...
    }

}
