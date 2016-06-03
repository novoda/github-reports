package com.novoda.github.reports.batch.persistence;

import com.novoda.github.reports.batch.issue.RepositoryIssue;
import com.novoda.github.reports.batch.persistence.converter.Converter;
import com.novoda.github.reports.data.EventDataLayer;
import com.novoda.github.reports.data.model.Event;

public class PersistIssueTransformer extends PersistTransformer<RepositoryIssue, Event> {

    private static final int ISSUE_BUFFER_SIZE = 100;

    public static PersistIssueTransformer newInstance(EventDataLayer eventDataLayer,
                                                      Converter<RepositoryIssue, Event> converter) {
        PersistIssuesOperator operator = PersistIssuesOperator.newInstance(eventDataLayer, converter);
        PersistBuffer buffer = PersistBuffer.newInstance(ISSUE_BUFFER_SIZE);
        return new PersistIssueTransformer(operator, buffer);
    }

    private PersistIssueTransformer(PersistOperator<RepositoryIssue, Event> operator, PersistBuffer buffer) {
        super(operator, buffer);
    }

}
