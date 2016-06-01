package com.novoda.github.reports.batch.persistence;

import com.novoda.github.reports.batch.repository.Repository;
import com.novoda.github.reports.data.RepoDataLayer;

import java.util.List;

import rx.Observable;
import rx.internal.util.UtilityFunctions;

public class PersistRepositoryTransformer implements Observable.Transformer<Repository, Repository> {
    private static final int REPOSITORY_BUFFER_SIZE = 100;
    private final Observable.Operator<List<Repository>, List<Repository>> operator;

    public static PersistRepositoryTransformer newInstance(RepoDataLayer repoDataLayer,
                                                           Converter<Repository, com.novoda.github.reports.data.model.Repository> converter) {
        PersistRepositoriesOperator operator = PersistRepositoriesOperator.newInstance(repoDataLayer, converter);
        return new PersistRepositoryTransformer(operator);
    }

    private PersistRepositoryTransformer(Observable.Operator<List<Repository>, List<Repository>> operator) {
        this.operator = operator;
    }

    @Override
    public Observable<Repository> call(Observable<Repository> repositoryObservable) {
        return repositoryObservable
                .buffer(REPOSITORY_BUFFER_SIZE)
                .lift(operator)
                .flatMapIterable(UtilityFunctions.identity());
    }
}
