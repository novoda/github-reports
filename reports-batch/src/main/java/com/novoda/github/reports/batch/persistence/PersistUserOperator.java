package com.novoda.github.reports.batch.persistence;

import com.novoda.github.reports.batch.issue.RepositoryIssue;
import com.novoda.github.reports.batch.persistence.converter.Converter;
import com.novoda.github.reports.data.DataLayer;
import com.novoda.github.reports.data.model.DatabaseUser;

class PersistUserOperator extends PersistOperator<RepositoryIssue, DatabaseUser> {

    public static PersistUserOperator newInstance(DataLayer<DatabaseUser> dataLayer, Converter<RepositoryIssue, DatabaseUser> converter) {
        return new PersistUserOperator(dataLayer, converter);
    }

    private PersistUserOperator(DataLayer<DatabaseUser> dataLayer, Converter<RepositoryIssue, DatabaseUser> converter) {
        super(dataLayer, converter);
    }

}
