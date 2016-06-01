package com.novoda.github.reports.batch.persistence;

import com.novoda.github.reports.batch.repository.Repository;
import com.novoda.github.reports.data.DataLayerException;
import com.novoda.github.reports.data.RepoDataLayer;

import rx.Observable;
import rx.Subscriber;
import rx.observers.SafeSubscriber;

public class PersistRepositoryOperator implements Observable.Operator<Repository, Repository> {
    private final RepoDataLayer repoDataLayer;
    private final Converter<Repository, com.novoda.github.reports.data.model.Repository> converter;

    public static PersistRepositoryOperator newInstance(RepoDataLayer repoDataLayer,
                                                        Converter<Repository, com.novoda.github.reports.data.model.Repository> converter) {
        return new PersistRepositoryOperator(repoDataLayer, converter);
    }

    private PersistRepositoryOperator(RepoDataLayer repoDataLayer,
                                      Converter<Repository, com.novoda.github.reports.data.model.Repository> converter) {
        this.repoDataLayer = repoDataLayer;
        this.converter = converter;
    }

    @Override
    public Subscriber<? super Repository> call(Subscriber<? super Repository> subscriber) {
        return new SafeSubscriber<>(new Subscriber<Repository>() {
            @Override
            public void onCompleted() {
                subscriber.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                subscriber.onError(e);
            }

            @Override
            public void onNext(Repository repository) {
                try {
                    repoDataLayer.updateOrInsert(converter.convertFrom(repository));
                    subscriber.onNext(repository);
                } catch (DataLayerException e) {
                    subscriber.onError(e);
                }
            }
        });
    }
}
