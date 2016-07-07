package com.novoda.github.reports.service.persistence;

import com.novoda.github.reports.data.EventDataLayer;
import com.novoda.github.reports.data.UserDataLayer;
import com.novoda.github.reports.data.db.ConnectionManager;
import com.novoda.github.reports.data.db.DbEventDataLayer;
import com.novoda.github.reports.data.db.DbUserDataLayer;
import com.novoda.github.reports.data.db.properties.DatabaseCredentialsReader;
import com.novoda.github.reports.data.model.Event;
import com.novoda.github.reports.data.model.User;
import com.novoda.github.reports.service.issue.RepositoryIssue;
import com.novoda.github.reports.service.persistence.converter.Converter;
import com.novoda.github.reports.service.persistence.converter.IssueConverter;
import com.novoda.github.reports.service.persistence.converter.UserConverter;

import rx.Observable;

public class RepositoryIssuePersistTransformer implements ComposedPersistTransformer<RepositoryIssue> {

    private final PersistTransformer<RepositoryIssue, User> persistUserTransformer;
    private final PersistTransformer<RepositoryIssue, Event> persistIssueTransformer;

    public static RepositoryIssuePersistTransformer newInstance(DatabaseCredentialsReader databaseCredentialsReader) {
        ConnectionManager connectionManager = ConnectionManagerContainer.getConnectionManager(databaseCredentialsReader);
        return buildRepositoryIssuePersistTransformer(connectionManager);
    }

    public static RepositoryIssuePersistTransformer newInstance() {
        ConnectionManager connectionManager = ConnectionManagerContainer.getConnectionManager();
        return buildRepositoryIssuePersistTransformer(connectionManager);
    }

    private static RepositoryIssuePersistTransformer buildRepositoryIssuePersistTransformer(ConnectionManager connectionManager) {
        UserDataLayer userDataLayer = DbUserDataLayer.newInstance(connectionManager);
        Converter<RepositoryIssue, User> userConverter = UserConverter.newInstance();
        PersistTransformer<RepositoryIssue, User> persistUserTransformer = PersistUserTransformer.newInstance(userDataLayer, userConverter);

        EventDataLayer eventDataLayer = DbEventDataLayer.newInstance(connectionManager);
        Converter<RepositoryIssue, Event> issueConverter = IssueConverter.newInstance();
        PersistTransformer<RepositoryIssue, Event> persistIssueTransformer = PersistIssueTransformer.newInstance(eventDataLayer, issueConverter);

        return new RepositoryIssuePersistTransformer(persistUserTransformer, persistIssueTransformer);
    }

    RepositoryIssuePersistTransformer(PersistTransformer<RepositoryIssue, User> persistUserTransformer,
                                      PersistTransformer<RepositoryIssue, Event> persistIssueTransformer) {

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
