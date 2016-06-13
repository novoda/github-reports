package com.novoda.github.reports.service.persistence;

import com.novoda.github.reports.data.EventDataLayer;
import com.novoda.github.reports.data.UserDataLayer;
import com.novoda.github.reports.data.db.ConnectionManager;
import com.novoda.github.reports.data.db.DbEventDataLayer;
import com.novoda.github.reports.data.db.DbUserDataLayer;
import com.novoda.github.reports.data.model.Event;
import com.novoda.github.reports.data.model.User;
import com.novoda.github.reports.service.issue.RepositoryIssueEvent;
import com.novoda.github.reports.service.persistence.converter.Converter;
import com.novoda.github.reports.service.persistence.converter.EventConverter;

import rx.Observable;

public class EventPersister implements Persister<RepositoryIssueEvent> {

    private final EventDataLayer eventDataLayer;
    private final UserDataLayer userDataLayer;
    private final Converter<RepositoryIssueEvent, User> eventUserConverter;
    private final Converter<RepositoryIssueEvent, Event> eventConverter;

    public static EventPersister newInstance() {
        ConnectionManager connectionManager = ConnectionManagerContainer.getConnectionManager();
        EventDataLayer eventDataLayer = DbEventDataLayer.newInstance(connectionManager);
        UserDataLayer userDataLayer = DbUserDataLayer.newInstance(connectionManager);
        Converter<RepositoryIssueEvent, User> eventUserConverter = EventUserConverter.newInstance();
        Converter<RepositoryIssueEvent, Event> eventConverter = EventConverter.newInstance();
        return new EventPersister(eventDataLayer, userDataLayer, eventUserConverter, eventConverter);
    }

    EventPersister(EventDataLayer eventDataLayer,
                   UserDataLayer userDataLayer,
                   Converter<RepositoryIssueEvent, User> eventUserConverter,
                   Converter<RepositoryIssueEvent, Event> eventConverter) {

        this.eventDataLayer = eventDataLayer;
        this.userDataLayer = userDataLayer;
        this.eventUserConverter = eventUserConverter;
        this.eventConverter = eventConverter;
    }

    @Override
    public Observable<RepositoryIssueEvent> call(Observable<RepositoryIssueEvent> observable) {
        return observable
                .compose(PersistEventUserTransformer.newInstance(userDataLayer, eventUserConverter))
                .compose(PersistEventTransformer.newInstance(eventDataLayer, eventConverter));
    }
}
