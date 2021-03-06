package com.novoda.github.reports.service.persistence;

import com.novoda.github.reports.data.EventDataLayer;
import com.novoda.github.reports.data.UserDataLayer;
import com.novoda.github.reports.data.db.ConnectionManager;
import com.novoda.github.reports.data.db.DbEventDataLayer;
import com.novoda.github.reports.data.db.DbUserDataLayer;
import com.novoda.github.reports.data.db.properties.DatabaseCredentialsReader;
import com.novoda.github.reports.data.model.Event;
import com.novoda.github.reports.data.model.User;
import com.novoda.github.reports.service.issue.RepositoryIssueEvent;
import com.novoda.github.reports.service.persistence.converter.Converter;
import com.novoda.github.reports.service.persistence.converter.EventConverter;

import rx.Observable;

public class RepositoryIssueEventPersistTransformer implements ComposedPersistTransformer<RepositoryIssueEvent> {

    private final PersistEventUserTransformer persistEventUserTransformer;
    private final PersistEventTransformer persistEventTransformer;

    public static RepositoryIssueEventPersistTransformer newInstance() {
        ConnectionManager connectionManager = ConnectionManagerContainer.getConnectionManager();
        return buildRepositoryIssueEventPersistTransformer(connectionManager);
    }

    public static RepositoryIssueEventPersistTransformer newInstance(DatabaseCredentialsReader databaseCredentialsReader) {
        ConnectionManager connectionManager = ConnectionManagerContainer.getConnectionManager(databaseCredentialsReader);
        return buildRepositoryIssueEventPersistTransformer(connectionManager);
    }

    private static RepositoryIssueEventPersistTransformer buildRepositoryIssueEventPersistTransformer(ConnectionManager connectionManager) {
        UserDataLayer userDataLayer = DbUserDataLayer.newInstance(connectionManager);
        Converter<RepositoryIssueEvent, User> eventUserConverter = EventUserConverter.newInstance();
        PersistEventUserTransformer persistEventUserTransformer = PersistEventUserTransformer.newInstance(userDataLayer, eventUserConverter);

        EventDataLayer eventDataLayer = DbEventDataLayer.newInstance(connectionManager);
        Converter<RepositoryIssueEvent, Event> eventConverter = EventConverter.newInstance();
        PersistEventTransformer persistEventTransformer = PersistEventTransformer.newInstance(eventDataLayer, eventConverter);

        return new RepositoryIssueEventPersistTransformer(persistEventUserTransformer, persistEventTransformer);
    }

    RepositoryIssueEventPersistTransformer(PersistEventUserTransformer persistEventUserTransformer, PersistEventTransformer persistEventTransformer) {
        this.persistEventUserTransformer = persistEventUserTransformer;
        this.persistEventTransformer = persistEventTransformer;
    }

    @Override
    public Observable<RepositoryIssueEvent> call(Observable<RepositoryIssueEvent> observable) {
        return observable
                .compose(persistEventUserTransformer)
                .compose(persistEventTransformer);
    }
}
