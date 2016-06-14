package com.novoda.github.reports.service.persistence;

import com.novoda.github.reports.data.EventDataLayer;
import com.novoda.github.reports.data.UserDataLayer;
import com.novoda.github.reports.data.db.ConnectionManager;
import com.novoda.github.reports.data.db.DbEventDataLayer;
import com.novoda.github.reports.data.db.DbUserDataLayer;
import com.novoda.github.reports.data.model.Event;
import com.novoda.github.reports.data.model.User;
import com.novoda.github.reports.service.issue.RepositoryIssue;
import com.novoda.github.reports.service.persistence.converter.Converter;
import com.novoda.github.reports.service.persistence.converter.IssueConverter;
import com.novoda.github.reports.service.persistence.converter.UserConverter;

import rx.Observable;

public class IssuePersister implements ComposedPersitTransformer<RepositoryIssue> {

    private final PersistUserTransformer persistUserTransformer;
    private final PersistIssueTransformer persistIssueTransformer;

    public static IssuePersister newInstance() {
        ConnectionManager connectionManager = ConnectionManagerContainer.getConnectionManager();

        UserDataLayer userDataLayer = DbUserDataLayer.newInstance(connectionManager);
        Converter<RepositoryIssue, User> userConverter = UserConverter.newInstance();
        PersistUserTransformer persistUserTransformer = PersistUserTransformer.newInstance(userDataLayer, userConverter);

        EventDataLayer eventDataLayer = DbEventDataLayer.newInstance(connectionManager);
        Converter<RepositoryIssue, Event> issueConverter = IssueConverter.newInstance();
        PersistIssueTransformer persistIssueTransformer = PersistIssueTransformer.newInstance(eventDataLayer, issueConverter);

        return new IssuePersister(persistUserTransformer, persistIssueTransformer);
    }

    IssuePersister(PersistUserTransformer persistUserTransformer, PersistIssueTransformer persistIssueTransformer) {
        this.persistUserTransformer = persistUserTransformer;
        this.persistIssueTransformer = persistIssueTransformer;
    }

    @Override
    public Observable<RepositoryIssue> call(Observable<RepositoryIssue> observable) {
        return observable
                .compose(persistUserTransformer)
                .compose(persistIssueTransformer);
    }
}
