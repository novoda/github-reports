package com.novoda.github.reports.batch.persistence;

import com.novoda.github.reports.batch.issue.RepositoryIssueEvent;
import com.novoda.github.reports.batch.persistence.converter.Converter;
import com.novoda.github.reports.data.EventDataLayer;
import com.novoda.github.reports.data.model.DatabaseEvent;

public class PersistEventTransformer extends PersistTransformer<RepositoryIssueEvent, DatabaseEvent> {

    private static final int EVENT_BUFFER_SIZE = 100;

    public static PersistEventTransformer newInstance(EventDataLayer eventDataLayer, Converter<RepositoryIssueEvent, DatabaseEvent> converter) {
        PersistEventsOperator operator = PersistEventsOperator.newInstance(eventDataLayer, converter);
        return new PersistEventTransformer(operator, EVENT_BUFFER_SIZE);
    }

    private PersistEventTransformer(PersistOperator<RepositoryIssueEvent, DatabaseEvent> operator, int bufferSize) {
        super(operator, bufferSize);
    }
}
