package com.novoda.github.reports.batch.persistence;

import com.novoda.github.reports.batch.issue.RepositoryIssueEvent;
import com.novoda.github.reports.batch.persistence.converter.Converter;
import com.novoda.github.reports.data.DataLayer;
import com.novoda.github.reports.data.model.DatabaseEvent;

class PersistEventsOperator extends PersistOperator<RepositoryIssueEvent, DatabaseEvent> {

    public static PersistEventsOperator newInstance(DataLayer<DatabaseEvent> dataLayer, Converter<RepositoryIssueEvent, DatabaseEvent> converter) {
        return new PersistEventsOperator(dataLayer, converter);
    }

    private PersistEventsOperator(DataLayer<DatabaseEvent> dataLayer, Converter<RepositoryIssueEvent, DatabaseEvent> converter) {
        super(dataLayer, converter);
    }
}
