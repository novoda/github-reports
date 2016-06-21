package com.novoda.github.reports.service.persistence;

import com.novoda.github.reports.service.issue.RepositoryIssue;
import com.novoda.github.reports.service.persistence.converter.Converter;
import com.novoda.github.reports.data.DataLayer;
import com.novoda.github.reports.data.model.User;

class PersistUserOperator extends PersistOperator<RepositoryIssue, User> {

    public static PersistUserOperator newInstance(DataLayer<User> dataLayer, Converter<RepositoryIssue, User> converter) {
        return new PersistUserOperator(dataLayer, converter);
    }

    private PersistUserOperator(DataLayer<User> dataLayer, Converter<RepositoryIssue, User> converter) {
        super(dataLayer, converter);
    }

}
