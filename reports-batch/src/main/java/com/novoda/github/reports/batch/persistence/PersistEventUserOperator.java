package com.novoda.github.reports.batch.persistence;

import com.novoda.github.reports.batch.issue.RepositoryIssueEvent;
import com.novoda.github.reports.batch.persistence.converter.Converter;
import com.novoda.github.reports.data.DataLayer;
import com.novoda.github.reports.data.model.DatabaseUser;

public class PersistEventUserOperator extends PersistOperator<RepositoryIssueEvent, DatabaseUser> {

    public static PersistEventUserOperator newInstance(DataLayer<DatabaseUser> dataLayer, Converter<RepositoryIssueEvent, DatabaseUser> converter) {
        return new PersistEventUserOperator(dataLayer, converter);
    }

    private PersistEventUserOperator(DataLayer<DatabaseUser> dataLayer, Converter<RepositoryIssueEvent, DatabaseUser> converter) {
        super(dataLayer, converter);
    }

}
