package com.novoda.github.reports.batch.persistence;

import com.novoda.github.reports.batch.repository.Repository;
import com.novoda.github.reports.data.RepoDataLayer;

import rx.Observable;
import rx.internal.util.UtilityFunctions;

public class PersistRepositoryTransformer implements Observable.Transformer<Repository, Repository> {
    private static final int REPOSITORY_BUFFER_SIZE = 100;
    private final RepoDataLayer repoDataLayer;
    private final Converter<Repository, com.novoda.github.reports.data.model.Repository> converter;

    public static PersistRepositoryTransformer newInstance(RepoDataLayer repoDataLayer,
                                                           Converter<Repository, com.novoda.github.reports.data.model.Repository> converter) {
        return new PersistRepositoryTransformer(repoDataLayer, converter);
    }

    private PersistRepositoryTransformer(RepoDataLayer repoDataLayer,
                                         Converter<Repository, com.novoda.github.reports.data.model.Repository> converter) {
        this.repoDataLayer = repoDataLayer;
        this.converter = converter;
    }

    @Override
    public Observable<Repository> call(Observable<Repository> repositoryObservable) {
        return repositoryObservable
                .buffer(REPOSITORY_BUFFER_SIZE)
                .lift(new PersistRepositoriesOperator(repoDataLayer, converter))
                .flatMapIterable(UtilityFunctions.identity());
    }
}
