package com.novoda.github.reports.batch.persistence;

import com.novoda.github.reports.batch.issue.RepositoryIssueEvent;
import com.novoda.github.reports.batch.persistence.converter.Converter;
import com.novoda.github.reports.data.UserDataLayer;
import com.novoda.github.reports.data.model.User;

public class PersistEventUserTransformer extends PersistTransformer<RepositoryIssueEvent, User> {

    private final static int USER_BUFFER_SIZE = 100;

    public static PersistEventUserTransformer newInstance(UserDataLayer userDataLayer,
                                                          Converter<RepositoryIssueEvent, User> converter) {
        PersistEventUserOperator operator = PersistEventUserOperator.newInstance(userDataLayer, converter);
        PersistBuffer buffer = PersistBuffer.newInstance(USER_BUFFER_SIZE);
        return new PersistEventUserTransformer(operator, buffer);
    }

    private PersistEventUserTransformer(PersistOperator<RepositoryIssueEvent, User> operator, PersistBuffer buffer) {
        super(operator, buffer);
    }

}
