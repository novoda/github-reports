package com.novoda.github.reports.batch.aws.repository;

import com.novoda.github.reports.aws.queue.AmazonGetIssuesQueueMessage;
import com.novoda.github.reports.aws.queue.AmazonGetRepositoriesQueueMessage;
import com.novoda.github.reports.aws.queue.AmazonQueueMessage;
import com.novoda.github.reports.batch.aws.persistence.PersistOperator;
import com.novoda.github.reports.data.RepoDataLayer;
import com.novoda.github.reports.data.db.ConnectionManager;
import com.novoda.github.reports.data.db.DbRepoDataLayer;
import com.novoda.github.reports.data.model.Repository;
import com.novoda.github.reports.service.network.LastPageExtractor;
import com.novoda.github.reports.service.network.NextPageExtractor;
import com.novoda.github.reports.service.persistence.ConnectionManagerContainer;
import com.novoda.github.reports.service.persistence.converter.Converter;
import com.novoda.github.reports.service.persistence.converter.RepositoryConverter;
import com.novoda.github.reports.service.repository.GithubRepository;
import com.novoda.github.reports.service.repository.GithubRepositoryService;
import com.novoda.github.reports.service.repository.RepositoryService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import retrofit2.Response;
import rx.Observable;
import rx.functions.Func1;

public class RepositoriesServiceClient {

    private static final int DEFAULT_PER_PAGE_COUNT = 100;

    private final RepositoryService repositoryService;
    private final RepoDataLayer repoDataLayer;
    private final Converter<GithubRepository, Repository> converter;

    private final NextPageExtractor nextPageExtractor;
    private final LastPageExtractor lastPageExtractor;

    public static RepositoriesServiceClient newInstance() {
        RepositoryService repositoriesService = GithubRepositoryService.newInstance();
        ConnectionManager connectionManager = ConnectionManagerContainer.getConnectionManager();
        RepoDataLayer repoDataLayer = DbRepoDataLayer.newInstance(connectionManager);
        Converter<GithubRepository, Repository> converter = RepositoryConverter.newInstance();
        NextPageExtractor nextPageExtractor = NextPageExtractor.newInstance();
        LastPageExtractor lastPageExtractor = LastPageExtractor.newInstance();
        return new RepositoriesServiceClient(repositoriesService, repoDataLayer, converter, nextPageExtractor, lastPageExtractor);
    }

    private RepositoriesServiceClient(RepositoryService repositoryService,
                                      RepoDataLayer repoDataLayer,
                                      Converter<GithubRepository, Repository> converter,
                                      NextPageExtractor nextPageExtractor,
                                      LastPageExtractor lastPageExtractor) {

        this.repositoryService = repositoryService;
        this.repoDataLayer = repoDataLayer;
        this.converter = converter;
        this.nextPageExtractor = nextPageExtractor;
        this.lastPageExtractor = lastPageExtractor;
    }

    public Observable<AmazonQueueMessage> getRepositoriesFor(AmazonGetRepositoriesQueueMessage message) {
        return repositoryService
                .getRepositoriesFor(message.organisationName(), Math.toIntExact(message.page()), DEFAULT_PER_PAGE_COUNT)
                .lift(new PersistOperator<>(repoDataLayer, converter))
                .flatMap(new Func1<Response<List<GithubRepository>>, Observable<AmazonQueueMessage>>() {
                    @Override
                    public Observable<AmazonQueueMessage> call(Response<List<GithubRepository>> response) {
                        List<AmazonQueueMessage> messages = new ArrayList<>();

                        if (message.localTerminal()) {
                            Optional<Integer> nextPage = nextPageExtractor.getNextPageFrom(response);
                            Optional<Integer> lastPage = lastPageExtractor.getLastPageFrom(response);

                            if (nextPage.isPresent()) {
                                int next = nextPage.get();
                                int last = lastPage.get();

                                for (int i = next; i <= last; i++) {
                                    messages.add(
                                            AmazonGetRepositoriesQueueMessage.create(
                                                    i != last,
                                                    (long) i,
                                                    message.receiptHandle(),
                                                    message.organisationName(),
                                                    message.sinceOrNull()
                                            ));
                                }
                            }
                        }

                        List<GithubRepository> repositories = response.body();
                        repositories.forEach(repository -> messages.add(AmazonGetIssuesQueueMessage.create(
                                true,
                                1L,
                                message.receiptHandle(),
                                message.organisationName(),
                                message.sinceOrNull(),
                                repository.getId(),
                                repository.getName()
                        )));

                        return Observable.from(messages);
                    }
                });
    }

}
