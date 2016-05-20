package com.novoda.github.reports.github.repository;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.observables.BlockingObservable;
import rx.schedulers.Schedulers;

public class RepositoriesServiceClient {

    private GithubRepositoriesService repositoriesService;

    public static RepositoriesServiceClient newInstance() {
        GithubRepositoriesService repositoriesService = GithubRepositoriesService.newInstance();
        return new RepositoriesServiceClient(repositoriesService);
    }

    private RepositoriesServiceClient(GithubRepositoriesService repositoriesService) {
        this.repositoriesService = repositoriesService;
    }

    public void getRepositoriesFrom(String organisation) {
        Observable<List<Repository>> observable = repositoriesService.getRepositoriesFrom(organisation)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.immediate());

        BlockingObservable<List<Repository>> blockingObservable = observable.toBlocking();

        blockingObservable.subscribe(new Subscriber<List<Repository>>() {
                    @Override
                    public void onCompleted() {
                        //
                    }

                    @Override
                    public void onError(Throwable e) {
                        //
                    }

                    @Override
                    public void onNext(List<Repository> repositories) {
                        repositories.forEach(repository -> System.out.println(repository.getName()));
                    }
                });
    }
}
