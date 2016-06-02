package com.novoda.github.reports.batch.persistence;

import com.novoda.github.reports.batch.repository.Repository;
import com.novoda.github.reports.data.DataLayerException;
import com.novoda.github.reports.data.RepoDataLayer;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.observers.SafeSubscriber;

class PersistRepositoriesOperator implements Observable.Operator<List<Repository>, List<Repository>> {
    private final RepoDataLayer repoDataLayer;
    private final Converter<Repository, com.novoda.github.reports.data.model.Repository> converter;

    public static PersistRepositoriesOperator newInstance(RepoDataLayer repoDataLayer,
                                                          Converter<Repository, com.novoda.github.reports.data.model.Repository> converter) {
        return new PersistRepositoriesOperator(repoDataLayer, converter);
    }

    private PersistRepositoriesOperator(RepoDataLayer repoDataLayer,
                                        Converter<Repository, com.novoda.github.reports.data.model.Repository> converter) {
        this.repoDataLayer = repoDataLayer;
        this.converter = converter;
    }

    @Override
    public Subscriber<? super List<Repository>> call(Subscriber<? super List<Repository>> subscriber) {
        return new SafeSubscriber<>(new Subscriber<List<Repository>>() {
            @Override
            public void onCompleted() {
                subscriber.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                subscriber.onError(e);
            }

            @Override
            public void onNext(List<Repository> repositories) {
                try {
                    repoDataLayer.updateOrInsert(converter.convertListFrom(repositories));
                    subscriber.onNext(repositories);
                } catch (DataLayerException e) {
                    subscriber.onError(e);
                }
            }
        });
    }
}
