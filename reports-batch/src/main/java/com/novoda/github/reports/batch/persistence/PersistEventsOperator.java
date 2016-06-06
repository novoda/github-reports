package com.novoda.github.reports.batch.persistence;

import com.novoda.github.reports.batch.issue.RepositoryIssueEvent;
import com.novoda.github.reports.batch.persistence.converter.Converter;
import com.novoda.github.reports.data.DataLayer;
import com.novoda.github.reports.data.model.Event;

class PersistEventsOperator extends PersistOperator<RepositoryIssueEvent, Event> {

    public static PersistEventsOperator newInstance(DataLayer<Event> dataLayer, Converter<RepositoryIssueEvent, Event> converter) {
        return new PersistEventsOperator(dataLayer, converter);
    }

    private PersistEventsOperator(DataLayer<Event> dataLayer, Converter<RepositoryIssueEvent, Event> converter) {
        super(dataLayer, converter);
    }
}
