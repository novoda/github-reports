package com.novoda.github.reports.batch.persistence;

import com.novoda.github.reports.batch.issue.RepositoryIssue;
import com.novoda.github.reports.batch.persistence.converter.Converter;
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
