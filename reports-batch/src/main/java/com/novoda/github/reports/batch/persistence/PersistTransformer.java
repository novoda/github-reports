package com.novoda.github.reports.batch.persistence;

import java.util.List;

import rx.Observable;
import rx.internal.util.UtilityFunctions;

class PersistTransformer<T, R> implements Observable.Transformer<T, T> {

    private final Observable.Operator<List<T>, List<T>> operator;
    private final PersistBuffer buffer;

    PersistTransformer(PersistOperator<T, R> operator, PersistBuffer buffer) {
        this.operator = operator;
        this.buffer = buffer;
    }

    @Override
    public Observable<T> call(Observable<T> repositoryObservable) {
        return repositoryObservable
                .buffer(buffer.getSize())
                .lift(operator)
                .flatMapIterable(UtilityFunctions.identity());
    }

}
