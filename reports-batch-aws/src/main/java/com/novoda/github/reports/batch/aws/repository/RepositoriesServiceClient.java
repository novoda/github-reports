package com.novoda.github.reports.batch.aws.repository;

import com.novoda.github.reports.aws.queue.AmazonGetRepositoriesQueueMessage;
import com.novoda.github.reports.aws.queue.AmazonQueueMessage;
import com.novoda.github.reports.aws.queue.QueueMessage;
import com.novoda.github.reports.batch.aws.persistence.PersistOperator;
import com.novoda.github.reports.data.RepoDataLayer;
import com.novoda.github.reports.data.db.ConnectionManager;
import com.novoda.github.reports.data.db.DbRepoDataLayer;
import com.novoda.github.reports.data.model.Repository;
import com.novoda.github.reports.service.persistence.ConnectionManagerContainer;
import com.novoda.github.reports.service.persistence.converter.Converter;
import com.novoda.github.reports.service.persistence.converter.RepositoryConverter;
import com.novoda.github.reports.service.repository.GithubRepository;
import com.novoda.github.reports.service.repository.GithubRepositoryService;
import com.novoda.github.reports.service.repository.RepositoryService;

import rx.Observable;

public class RepositoriesServiceClient {

    private static final int DEFAULT_PER_PAGE_COUNT = 100;

    private final RepositoryService repositoryService;
    private final RepoDataLayer repoDataLayer;
    private final Converter<GithubRepository, Repository> converter;


    public static RepositoriesServiceClient newInstance() {
        RepositoryService repositoriesService = GithubRepositoryService.newInstance();
        ConnectionManager connectionManager = ConnectionManagerContainer.getConnectionManager();
        RepoDataLayer repoDataLayer = DbRepoDataLayer.newInstance(connectionManager);
        Converter<GithubRepository, Repository> converter = RepositoryConverter.newInstance();
        return new RepositoriesServiceClient(repositoriesService, repoDataLayer, converter);
    }

    private RepositoriesServiceClient(RepositoryService repositoryService,
                                      RepoDataLayer repoDataLayer,
                                      Converter<GithubRepository, Repository> converter) {

        this.repositoryService = repositoryService;
        this.repoDataLayer = repoDataLayer;
        this.converter = converter;
    }

    public Observable<AmazonQueueMessage> getRepositoriesFor(AmazonGetRepositoriesQueueMessage message) {
        return repositoryService
                .getRepositoriesFor(message.organisationName(), pageFrom(message), DEFAULT_PER_PAGE_COUNT)
                .lift(new PersistOperator<>(repoDataLayer, converter))
                .compose(NextMessagesRepositoryTransformer.newInstance(message));
    }

    private int pageFrom(QueueMessage message) {
        return Math.toIntExact(message.page());
    }

}
