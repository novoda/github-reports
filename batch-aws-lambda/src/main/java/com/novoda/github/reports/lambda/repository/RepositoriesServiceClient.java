package com.novoda.github.reports.lambda.repository;

import com.novoda.github.reports.batch.aws.queue.AmazonGetRepositoriesQueueMessage;
import com.novoda.github.reports.batch.aws.queue.AmazonQueueMessage;
import com.novoda.github.reports.batch.queue.QueueMessage;
import com.novoda.github.reports.data.db.ConnectionManager;
import com.novoda.github.reports.data.db.DbDataLayer;
import com.novoda.github.reports.data.db.DbRepoDataLayer;
import com.novoda.github.reports.data.db.properties.DatabaseCredentialsReader;
import com.novoda.github.reports.data.db.tables.records.RepositoryRecord;
import com.novoda.github.reports.data.model.Repository;
import com.novoda.github.reports.lambda.persistence.PersistOperator;
import com.novoda.github.reports.service.persistence.ConnectionManagerContainer;
import com.novoda.github.reports.service.persistence.converter.Converter;
import com.novoda.github.reports.service.persistence.converter.RepositoryConverter;
import com.novoda.github.reports.service.properties.GithubCredentialsReader;
import com.novoda.github.reports.service.repository.GithubRepository;
import com.novoda.github.reports.service.repository.GithubRepositoryService;
import com.novoda.github.reports.service.repository.RepositoryService;
import rx.Observable;

public class RepositoriesServiceClient {

    private static final int DEFAULT_PER_PAGE_COUNT = 100;

    private final RepositoryService repositoryService;
    private final DbDataLayer<Repository, RepositoryRecord> repoDataLayer;
    private final Converter<GithubRepository, Repository> converter;

    public static RepositoriesServiceClient newInstance() {
        RepositoryService repositoriesService = GithubRepositoryService.newInstance();
        ConnectionManager connectionManager = ConnectionManagerContainer.getConnectionManager();
        DbDataLayer<Repository, RepositoryRecord> repoDataLayer = DbRepoDataLayer.newInstance(connectionManager);
        Converter<GithubRepository, Repository> converter = RepositoryConverter.newInstance();

        return new RepositoriesServiceClient(repositoriesService, repoDataLayer, converter);
    }

    public static RepositoriesServiceClient newInstance(GithubCredentialsReader githubCredentialsReader,
                                                        DatabaseCredentialsReader databaseCredentialsReader) {

        RepositoryService repositoriesService = GithubRepositoryService.newInstance(githubCredentialsReader);
        ConnectionManager connectionManager = ConnectionManagerContainer.getConnectionManager(databaseCredentialsReader);
        DbDataLayer<Repository, RepositoryRecord> repoDataLayer = DbRepoDataLayer.newInstance(connectionManager);
        Converter<GithubRepository, Repository> converter = RepositoryConverter.newInstance();

        return new RepositoriesServiceClient(repositoriesService, repoDataLayer, converter);
    }

    private RepositoriesServiceClient(RepositoryService repositoryService,
                                      DbDataLayer<Repository, RepositoryRecord> repoDataLayer,
                                      Converter<GithubRepository, Repository> converter) {

        this.repositoryService = repositoryService;
        this.repoDataLayer = repoDataLayer;
        this.converter = converter;
    }

    public Observable<AmazonQueueMessage> retrieveRepositoriesFor(AmazonGetRepositoriesQueueMessage message) {
        return repositoryService
                .getRepositoriesFor(message.organisationName(), pageFrom(message), DEFAULT_PER_PAGE_COUNT)
                .lift(persistRepositories())
                .compose(getNextQueueMessages(message));
    }

    private int pageFrom(QueueMessage message) {
        return Math.toIntExact(message.page());
    }

    private PersistOperator<GithubRepository, Repository> persistRepositories() {
        return new PersistOperator<>(repoDataLayer, converter);
    }

    private NextMessagesRepositoryTransformer getNextQueueMessages(AmazonGetRepositoriesQueueMessage message) {
        return NextMessagesRepositoryTransformer.newInstance(message);
    }

}
