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

public class IssuePersister implements Persister<RepositoryIssue> {

    private final EventDataLayer eventDataLayer;
    private final Converter<RepositoryIssue, Event> issueConverter;
    private final UserDataLayer userDataLayer;
    private final Converter<RepositoryIssue, User> userConverter;

    public static IssuePersister newInstance() {
        ConnectionManager connectionManager = ConnectionManagerContainer.getConnectionManager();
        EventDataLayer eventDataLayer = DbEventDataLayer.newInstance(connectionManager);
        Converter<RepositoryIssue, Event> issueConverter = IssueConverter.newInstance();
        UserDataLayer userDataLayer = DbUserDataLayer.newInstance(connectionManager);
        Converter<RepositoryIssue, User> userConverter = UserConverter.newInstance();
        return new IssuePersister(eventDataLayer, issueConverter, userDataLayer, userConverter);
    }

    IssuePersister(EventDataLayer eventDataLayer,
                   Converter<RepositoryIssue, Event> issueConverter,
                   UserDataLayer userDataLayer,
                   Converter<RepositoryIssue, User> userConverter) {

        this.eventDataLayer = eventDataLayer;
        this.issueConverter = issueConverter;
        this.userDataLayer = userDataLayer;
        this.userConverter = userConverter;
    }

    @Override
    public Observable<RepositoryIssue> call(Observable<RepositoryIssue> observable) {
        return observable
                .compose(PersistUserTransformer.newInstance(userDataLayer, userConverter))
                .compose(PersistIssueTransformer.newInstance(eventDataLayer, issueConverter));
    }
}
