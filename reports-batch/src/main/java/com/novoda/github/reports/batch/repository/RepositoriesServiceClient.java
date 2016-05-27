package com.novoda.github.reports.batch.repository;

import rx.Observable;
import rx.schedulers.Schedulers;

public class RepositoriesServiceClient {

    private RepositoryService repositoryService;

    public static RepositoriesServiceClient newInstance() {
        GithubRepositoriesService repositoriesService = GithubRepositoriesService.newInstance();
        return new RepositoriesServiceClient(repositoriesService);
    }

    private RepositoriesServiceClient(GithubRepositoriesService repositoryService) {
        this.repositoryService = repositoryService;
    }

    public Observable<Repository> getRepositoriesFrom(String organisation) {
        return repositoryService.getPagedRepositoriesFor(organisation)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.immediate());
    }
}
