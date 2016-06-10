package com.novoda.github.reports.batch.aws.repository;

import com.novoda.github.reports.service.repository.GithubRepositoryService;
import com.novoda.github.reports.service.repository.GithubRepository;
import com.novoda.github.reports.service.repository.RepositoryService;

import retrofit2.Response;
import rx.Observable;

public class RepositoryServiceClient {

    private static final int DEFAULT_PER_PAGE_COUNT = 100;

    private final RepositoryService repositoryService;

    public static RepositoryServiceClient newInstance() {
        RepositoryService repositoriesService = GithubRepositoryService.newInstance();
        return new RepositoryServiceClient(repositoriesService);
    }

    private RepositoryServiceClient(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    public Observable<GithubRepository> getRepositoriesFor(String organisation, int page) {
        return repositoryService.getRepositoriesFor(organisation, page, DEFAULT_PER_PAGE_COUNT)
                .flatMapIterable(Response::body);
    }

}
