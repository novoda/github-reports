package com.novoda.github.reports.batch.persistence;

import com.novoda.github.reports.batch.persistence.converter.Converter;
import com.novoda.github.reports.batch.repository.Repository;
import com.novoda.github.reports.data.DataLayer;
import com.novoda.github.reports.data.RepoDataLayer;

class PersistRepositoriesOperator extends PersistOperator<Repository, com.novoda.github.reports.data.model.Repository> {

    public static PersistRepositoriesOperator newInstance(RepoDataLayer repoDataLayer,
                                                          Converter<Repository, com.novoda.github.reports.data.model.Repository> converter) {
        return new PersistRepositoriesOperator(repoDataLayer, converter);
    }

    private PersistRepositoriesOperator(DataLayer<com.novoda.github.reports.data.model.Repository> dataLayer,
                                        Converter<Repository, com.novoda.github.reports.data.model.Repository> converter) {
        super(dataLayer, converter);
    }

}
