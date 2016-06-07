package com.novoda.github.reports.batch.persistence;

import com.novoda.github.reports.batch.persistence.converter.Converter;
import com.novoda.github.reports.batch.repository.Repository;
import com.novoda.github.reports.data.RepoDataLayer;
import com.novoda.github.reports.data.model.DatabaseRepository;

public class PersistRepositoryTransformer extends PersistTransformer<Repository, DatabaseRepository> {

    private static final int REPOSITORY_BUFFER_SIZE = 100;

    public static PersistRepositoryTransformer newInstance(RepoDataLayer repoDataLayer, Converter<Repository, DatabaseRepository> converter) {
        PersistRepositoriesOperator operator = PersistRepositoriesOperator.newInstance(repoDataLayer, converter);
        return new PersistRepositoryTransformer(operator, REPOSITORY_BUFFER_SIZE);
    }

    private PersistRepositoryTransformer(PersistOperator<Repository, DatabaseRepository> operator, int bufferSize) {
        super(operator, bufferSize);
    }

}
