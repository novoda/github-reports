package com.novoda.github.reports.batch.persistence;

import com.novoda.github.reports.batch.issue.RepositoryIssue;
import com.novoda.github.reports.batch.persistence.converter.Converter;
import com.novoda.github.reports.data.DataLayer;
import com.novoda.github.reports.data.EventDataLayer;
import com.novoda.github.reports.data.model.DatabaseEvent;

class PersistIssuesOperator extends PersistOperator<RepositoryIssue, DatabaseEvent> {

    private PersistIssuesOperator(DataLayer<DatabaseEvent> dataLayer, Converter<RepositoryIssue, DatabaseEvent> converter) {
        super(dataLayer, converter);
    }

    public static PersistIssuesOperator newInstance(EventDataLayer eventDataLayer,
                                                    Converter<RepositoryIssue, DatabaseEvent> converter) {
        return new PersistIssuesOperator(eventDataLayer, converter);
    }

}
