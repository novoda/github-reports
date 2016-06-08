package com.novoda.github.reports.service.persistence;

import com.novoda.github.reports.service.issue.RepositoryIssueEvent;
import com.novoda.github.reports.service.persistence.converter.Converter;
import com.novoda.github.reports.data.UserDataLayer;
import com.novoda.github.reports.data.model.User;

public class PersistEventUserTransformer extends PersistTransformer<RepositoryIssueEvent, User> {

    private final static int USER_BUFFER_SIZE = 100;

    public static PersistEventUserTransformer newInstance(UserDataLayer userDataLayer, Converter<RepositoryIssueEvent, User> converter) {
        PersistEventUserOperator operator = PersistEventUserOperator.newInstance(userDataLayer, converter);
        return new PersistEventUserTransformer(operator, USER_BUFFER_SIZE);
    }

    private PersistEventUserTransformer(PersistOperator<RepositoryIssueEvent, User> operator, int bufferSize) {
        super(operator, bufferSize);
    }

}
