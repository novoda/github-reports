package com.novoda.github.reports.batch.persistence;

import com.novoda.github.reports.batch.issue.RepositoryIssueEvent;
import com.novoda.github.reports.batch.persistence.converter.Converter;
import com.novoda.github.reports.data.EventDataLayer;
import com.novoda.github.reports.data.model.Event;

public class PersistEventTransformer extends PersistTransformer<RepositoryIssueEvent, Event> {

    private static final int EVENT_BUFFER_SIZE = 100;

    public static PersistEventTransformer newInstance(
            EventDataLayer eventDataLayer, Converter<RepositoryIssueEvent, Event> converter) {
        PersistEventsOperator operator = PersistEventsOperator.newInstance(eventDataLayer, converter);
        PersistBuffer buffer = PersistBuffer.newInstance(EVENT_BUFFER_SIZE);
        return new PersistEventTransformer(operator, buffer);
    }

    private PersistEventTransformer(PersistOperator<RepositoryIssueEvent, Event> operator, PersistBuffer buffer) {
        super(operator, buffer);
    }
}
