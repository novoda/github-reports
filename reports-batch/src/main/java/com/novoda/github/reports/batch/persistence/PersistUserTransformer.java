package com.novoda.github.reports.batch.persistence;

import com.novoda.github.reports.batch.issue.RepositoryIssue;
import com.novoda.github.reports.batch.persistence.converter.Converter;
import com.novoda.github.reports.data.UserDataLayer;
import com.novoda.github.reports.data.model.User;

public class PersistUserTransformer extends PersistTransformer<RepositoryIssue, User> {

    private final static int USER_BUFFER_SIZE = 100;

    public static PersistUserTransformer newInstance(UserDataLayer userDataLayer,
                                                     Converter<RepositoryIssue, User> converter) {
        PersistUserOperator operator = PersistUserOperator.newInstance(userDataLayer, converter);
        PersistBuffer buffer = PersistBuffer.newInstance(USER_BUFFER_SIZE);
        return new PersistUserTransformer(operator, buffer);
    }

    private PersistUserTransformer(PersistOperator<RepositoryIssue, User> operator, PersistBuffer buffer) {
        super(operator, buffer);
    }

}
