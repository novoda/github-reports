package com.novoda.github.reports.github.repository;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class RepositoriesServiceClient {

    private RepositoryService repositoriesService;

    public static RepositoriesServiceClient newInstance() {
        GithubRepositoriesService repositoriesService = GithubRepositoriesService.newInstance();
        return new RepositoriesServiceClient(repositoriesService);
    }

    private RepositoriesServiceClient(GithubRepositoriesService repositoriesService) {
        this.repositoriesService = repositoriesService;
    }

    public Observable<List<Repository>> getListOfRepositoriesFrom(String organisation) {
        return repositoriesService.getRepositoriesFrom(organisation)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.immediate());
    }

    public Observable<Repository> getRepositoriesFrom(String organisation) {
        return repositoriesService.getRepositoriesFrom(organisation)
                .flatMapIterable((Func1<List<Repository>, Iterable<Repository>>) repositories -> repositories)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.immediate());
    }
}
