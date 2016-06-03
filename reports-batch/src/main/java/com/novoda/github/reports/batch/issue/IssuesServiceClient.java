package com.novoda.github.reports.batch.issue;

import com.novoda.github.reports.batch.persistence.ConnectionManagerContainer;
import com.novoda.github.reports.batch.persistence.converter.Converter;
import com.novoda.github.reports.batch.persistence.converter.IssueConverter;
import com.novoda.github.reports.batch.persistence.PersistIssueTransformer;
import com.novoda.github.reports.batch.persistence.PersistUserTransformer;
import com.novoda.github.reports.batch.persistence.converter.UserConverter;
import com.novoda.github.reports.batch.repository.Repository;
import com.novoda.github.reports.data.EventDataLayer;
import com.novoda.github.reports.data.UserDataLayer;
import com.novoda.github.reports.data.db.ConnectionManager;
import com.novoda.github.reports.data.db.DbEventDataLayer;
import com.novoda.github.reports.data.db.DbUserDataLayer;

import java.util.Date;

import rx.Observable;
import rx.schedulers.Schedulers;

public class IssuesServiceClient {

    private final IssueService issueService;
    private final EventDataLayer eventDataLayer;
    private final Converter<RepositoryIssue, com.novoda.github.reports.data.model.Event> issueConverter;
    private final UserDataLayer userDataLayer;
    private final Converter<RepositoryIssue, com.novoda.github.reports.data.model.User> userConverter;

    public static IssuesServiceClient newInstance() {
        IssueService issueService = GithubIssueService.newInstance();
        ConnectionManager connectionManager = ConnectionManagerContainer.getConnectionManager();

        EventDataLayer eventDataLayer = DbEventDataLayer.newInstance(connectionManager);
        Converter<RepositoryIssue, com.novoda.github.reports.data.model.Event> issueConverter = IssueConverter.newInstance();
        UserDataLayer userDataLayer = DbUserDataLayer.newInstance(connectionManager);
        Converter<RepositoryIssue, com.novoda.github.reports.data.model.User> userConverter = UserConverter.newInstance();

        return new IssuesServiceClient(issueService, eventDataLayer, issueConverter, userDataLayer, userConverter);
    }

    private IssuesServiceClient(IssueService issueService,
                                EventDataLayer eventDataLayer,
                                Converter<RepositoryIssue, com.novoda.github.reports.data.model.Event> issueConverter,
                                UserDataLayer userDataLayer,
                                Converter<RepositoryIssue, com.novoda.github.reports.data.model.User> userConverter) {
        this.issueService = issueService;
        this.eventDataLayer = eventDataLayer;
        this.issueConverter = issueConverter;
        this.userDataLayer = userDataLayer;
        this.userConverter = userConverter;
    }

    public Observable<RepositoryIssue> retrieveIssuesFrom(String organisation, Repository repository, Date since) {
        return issueService.getPagedIssuesFor(organisation, repository.getName(), since)
                .map(issue -> RepositoryIssue.newInstance(repository, issue))
                .compose(PersistUserTransformer.newInstance(userDataLayer, userConverter))
                .compose(PersistIssueTransformer.newInstance(eventDataLayer, issueConverter))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.immediate());
    }

    public Observable<Event> getEventsFrom(String organisation, String repository, Integer issueNumber) {
        return issueService.getPagedEventsFor(organisation, repository, issueNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.immediate());
    }

    public Observable<Comment> getCommentsFrom(String organisation, String repository, Integer issueNumber) {
        return issueService.getPagedCommentsFor(organisation, repository, issueNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.immediate());
    }
}
