package com.novoda.github.reports.batch.persistence;

import com.novoda.github.reports.batch.issue.RepositoryIssue;
import com.novoda.github.reports.batch.persistence.converter.Converter;
import com.novoda.github.reports.data.EventDataLayer;
import com.novoda.github.reports.data.model.DatabaseEvent;

public class PersistIssueTransformer extends PersistTransformer<RepositoryIssue, DatabaseEvent> {

    private static final int ISSUE_BUFFER_SIZE = 100;

    public static PersistIssueTransformer newInstance(EventDataLayer eventDataLayer,
                                                      Converter<RepositoryIssue, DatabaseEvent> converter) {
        PersistIssuesOperator operator = PersistIssuesOperator.newInstance(eventDataLayer, converter);
        return new PersistIssueTransformer(operator, ISSUE_BUFFER_SIZE);
    }

    private PersistIssueTransformer(PersistOperator<RepositoryIssue, DatabaseEvent> operator, int bufferSize) {
        super(operator, bufferSize);
    }

}
