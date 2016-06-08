package com.novoda.github.reports.service.persistence;

import com.novoda.github.reports.service.issue.RepositoryIssueEvent;
import com.novoda.github.reports.service.persistence.converter.Converter;
import com.novoda.github.reports.data.EventDataLayer;
import com.novoda.github.reports.data.model.Event;

public class PersistEventTransformer extends PersistTransformer<RepositoryIssueEvent, Event> {

    private static final int EVENT_BUFFER_SIZE = 100;

    public static PersistEventTransformer newInstance(EventDataLayer eventDataLayer, Converter<RepositoryIssueEvent, Event> converter) {
        PersistEventsOperator operator = PersistEventsOperator.newInstance(eventDataLayer, converter);
        return new PersistEventTransformer(operator, EVENT_BUFFER_SIZE);
    }

    private PersistEventTransformer(PersistOperator<RepositoryIssueEvent, Event> operator, int bufferSize) {
        super(operator, bufferSize);
    }
}
