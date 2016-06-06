package com.novoda.github.reports.batch.persistence;

import java.util.List;

import rx.Observable;
import rx.internal.util.UtilityFunctions;

class PersistTransformer<T, R> implements Observable.Transformer<T, T> {

    private final Observable.Operator<List<T>, List<T>> operator;
    private final int bufferSize;

    PersistTransformer(PersistOperator<T, R> operator, int bufferSize) {
        this.operator = operator;
        this.bufferSize = bufferSize;
    }

    @Override
    public Observable<T> call(Observable<T> repositoryObservable) {
        return repositoryObservable
                .buffer(bufferSize)
                .lift(operator)
                .flatMapIterable(UtilityFunctions.identity());
    }

}
