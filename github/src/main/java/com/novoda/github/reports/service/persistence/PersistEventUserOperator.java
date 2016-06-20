package com.novoda.github.reports.service.persistence;

import com.novoda.github.reports.service.issue.RepositoryIssueEvent;
import com.novoda.github.reports.service.persistence.converter.Converter;
import com.novoda.github.reports.data.DataLayer;
import com.novoda.github.reports.data.model.User;

class PersistEventUserOperator extends PersistOperator<RepositoryIssueEvent, User> {

    public static PersistEventUserOperator newInstance(DataLayer<User> dataLayer, Converter<RepositoryIssueEvent, User> converter) {
        return new PersistEventUserOperator(dataLayer, converter);
    }

    private PersistEventUserOperator(DataLayer<User> dataLayer, Converter<RepositoryIssueEvent, User> converter) {
        super(dataLayer, converter);
    }

}
