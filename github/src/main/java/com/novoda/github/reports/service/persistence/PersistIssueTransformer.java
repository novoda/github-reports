package com.novoda.github.reports.service.persistence;

import com.novoda.github.reports.service.issue.RepositoryIssue;
import com.novoda.github.reports.service.persistence.converter.Converter;
import com.novoda.github.reports.data.EventDataLayer;
import com.novoda.github.reports.data.model.Event;

class PersistIssueTransformer extends PersistTransformer<RepositoryIssue, Event> {

    private static final int ISSUE_BUFFER_SIZE = 100;

    public static PersistIssueTransformer newInstance(EventDataLayer eventDataLayer,
                                                      Converter<RepositoryIssue, Event> converter) {
        PersistIssuesOperator operator = PersistIssuesOperator.newInstance(eventDataLayer, converter);
        return new PersistIssueTransformer(operator, ISSUE_BUFFER_SIZE);
    }

    private PersistIssueTransformer(PersistOperator<RepositoryIssue, Event> operator, int bufferSize) {
        super(operator, bufferSize);
    }

}
