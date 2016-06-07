package com.novoda.github.reports.batch.persistence;

import com.novoda.github.reports.batch.issue.RepositoryIssue;
import com.novoda.github.reports.batch.persistence.converter.Converter;
import com.novoda.github.reports.data.UserDataLayer;
import com.novoda.github.reports.data.model.DatabaseUser;

public class PersistUserTransformer extends PersistTransformer<RepositoryIssue, DatabaseUser> {

    private final static int USER_BUFFER_SIZE = 100;

    public static PersistUserTransformer newInstance(UserDataLayer userDataLayer, Converter<RepositoryIssue, DatabaseUser> converter) {
        PersistUserOperator operator = PersistUserOperator.newInstance(userDataLayer, converter);
        return new PersistUserTransformer(operator, USER_BUFFER_SIZE);
    }

    private PersistUserTransformer(PersistOperator<RepositoryIssue, DatabaseUser> operator, int bufferSize) {
        super(operator, bufferSize);
    }

}
