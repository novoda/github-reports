package com.novoda.github.reports.batch.persistence;

import com.novoda.github.reports.batch.issue.RepositoryIssueEvent;
import com.novoda.github.reports.batch.persistence.converter.Converter;
import com.novoda.github.reports.data.UserDataLayer;
import com.novoda.github.reports.data.model.DatabaseUser;

public class PersistEventUserTransformer extends PersistTransformer<RepositoryIssueEvent, DatabaseUser> {

    private final static int USER_BUFFER_SIZE = 100;

    public static PersistEventUserTransformer newInstance(UserDataLayer userDataLayer, Converter<RepositoryIssueEvent, DatabaseUser> converter) {
        PersistEventUserOperator operator = PersistEventUserOperator.newInstance(userDataLayer, converter);
        return new PersistEventUserTransformer(operator, USER_BUFFER_SIZE);
    }

    private PersistEventUserTransformer(PersistOperator<RepositoryIssueEvent, DatabaseUser> operator, int bufferSize) {
        super(operator, bufferSize);
    }

}
