package com.novoda.github.reports.github;

import com.novoda.github.reports.github.repository.Repository;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.observables.BlockingObservable;
import rx.schedulers.Schedulers;

public class RepositoriesServiceClient {

    private RepositoriesService repositoriesService;

    public static RepositoriesServiceClient newInstance() {
        RepositoriesService repositoriesService = RepositoriesService.newInstance();
        return new RepositoriesServiceClient(repositoriesService);
    }

    RepositoriesServiceClient(RepositoriesService repositoriesService) {
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
