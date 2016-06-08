package com.novoda.github.reports.batch.persistence;

import com.novoda.github.reports.batch.issue.RepositoryIssue;
import com.novoda.github.reports.batch.persistence.converter.Converter;
import com.novoda.github.reports.data.DataLayer;
import com.novoda.github.reports.data.EventDataLayer;
import com.novoda.github.reports.data.model.Event;

class PersistIssuesOperator extends PersistOperator<RepositoryIssue, Event> {

    private PersistIssuesOperator(DataLayer<Event> dataLayer, Converter<RepositoryIssue, Event> converter) {
        super(dataLayer, converter);
    }

    public static PersistIssuesOperator newInstance(EventDataLayer eventDataLayer,
                                                    Converter<RepositoryIssue, Event> converter) {
        return new PersistIssuesOperator(eventDataLayer, converter);
    }

}
