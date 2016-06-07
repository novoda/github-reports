package com.novoda.github.reports.batch.persistence;

import com.novoda.github.reports.batch.persistence.converter.Converter;
import com.novoda.github.reports.batch.repository.Repository;
import com.novoda.github.reports.data.DataLayer;
import com.novoda.github.reports.data.RepoDataLayer;
import com.novoda.github.reports.data.model.DatabaseRepository;

class PersistRepositoriesOperator extends PersistOperator<Repository, DatabaseRepository> {

    public static PersistRepositoriesOperator newInstance(RepoDataLayer repoDataLayer,
                                                          Converter<Repository, DatabaseRepository> converter) {
        return new PersistRepositoriesOperator(repoDataLayer, converter);
    }

    private PersistRepositoriesOperator(DataLayer<DatabaseRepository> dataLayer,
                                        Converter<Repository, DatabaseRepository> converter) {
        super(dataLayer, converter);
    }

}
