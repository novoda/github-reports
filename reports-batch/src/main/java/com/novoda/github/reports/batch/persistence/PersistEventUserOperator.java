package com.novoda.github.reports.batch.persistence;

import com.novoda.github.reports.batch.issue.RepositoryIssueEvent;
import com.novoda.github.reports.batch.persistence.converter.Converter;
import com.novoda.github.reports.data.DataLayer;
import com.novoda.github.reports.data.model.User;

public class PersistEventUserOperator extends PersistOperator<RepositoryIssueEvent, User> {

    public static PersistEventUserOperator newInstance(DataLayer<User> dataLayer, Converter<RepositoryIssueEvent, User> converter) {
        return new PersistEventUserOperator(dataLayer, converter);
    }

    private PersistEventUserOperator(DataLayer<User> dataLayer, Converter<RepositoryIssueEvent, User> converter) {
        super(dataLayer, converter);
    }

}
