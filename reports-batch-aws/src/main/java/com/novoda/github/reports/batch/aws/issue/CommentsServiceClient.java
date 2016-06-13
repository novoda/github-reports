package com.novoda.github.reports.batch.aws.issue;

import com.novoda.github.reports.data.EventDataLayer;
import com.novoda.github.reports.data.UserDataLayer;
import com.novoda.github.reports.data.db.ConnectionManager;
import com.novoda.github.reports.data.db.DbEventDataLayer;
import com.novoda.github.reports.data.db.DbUserDataLayer;
import com.novoda.github.reports.data.model.Event;
import com.novoda.github.reports.data.model.User;
import com.novoda.github.reports.service.issue.GithubIssueService;
import com.novoda.github.reports.service.issue.IssueService;
import com.novoda.github.reports.service.issue.RepositoryIssue;
import com.novoda.github.reports.service.issue.RepositoryIssueEvent;
import com.novoda.github.reports.service.issue.RepositoryIssueEventComment;
import com.novoda.github.reports.service.network.DateToISO8601Converter;
import com.novoda.github.reports.service.persistence.ConnectionManagerContainer;
import com.novoda.github.reports.service.persistence.EventUserConverter;
import com.novoda.github.reports.service.persistence.PersistEventTransformer;
import com.novoda.github.reports.service.persistence.PersistEventUserTransformer;
import com.novoda.github.reports.service.persistence.converter.Converter;
import com.novoda.github.reports.service.persistence.converter.EventConverter;

import java.util.Date;

import retrofit2.Response;
import rx.Observable;

public class CommentsServiceClient {

    private static final int DEFAULT_PER_PAGE_COUNT = 100;

    private final IssueService issueService;
    private final DateToISO8601Converter dateConverter;

    private final EventDataLayer eventDataLayer;
    private final UserDataLayer userDataLayer;
    private final Converter<RepositoryIssueEvent, User> eventUserConverter;
    private final Converter<RepositoryIssueEvent, Event> eventConverter;

    public static CommentsServiceClient newInstance() {
        IssueService issueService = GithubIssueService.newInstance();
        DateToISO8601Converter dateConverter = new DateToISO8601Converter();

        ConnectionManager connectionManager = ConnectionManagerContainer.getConnectionManager();
        EventDataLayer eventDataLayer = DbEventDataLayer.newInstance(connectionManager);
        UserDataLayer userDataLayer = DbUserDataLayer.newInstance(connectionManager);
        Converter<RepositoryIssueEvent, User> eventUserConverter = EventUserConverter.newInstance();
        Converter<RepositoryIssueEvent, Event> eventConverter = EventConverter.newInstance();

        return new CommentsServiceClient(issueService, dateConverter, eventDataLayer, userDataLayer, eventUserConverter, eventConverter);
    }

    public CommentsServiceClient(IssueService issueService,
                                 DateToISO8601Converter dateConverter,
                                 EventDataLayer eventDataLayer,
                                 UserDataLayer userDataLayer,
                                 Converter<RepositoryIssueEvent, User> eventUserConverter,
                                 Converter<RepositoryIssueEvent, Event> eventConverter) {

        this.issueService = issueService;
        this.dateConverter = dateConverter;
        this.eventDataLayer = eventDataLayer;
        this.userDataLayer = userDataLayer;
        this.eventUserConverter = eventUserConverter;
        this.eventConverter = eventConverter;
    }

    // FIXME we should return something more specific than "RepositoryIssueEvent"
    public Observable<RepositoryIssueEvent> retrieveCommentsFrom(RepositoryIssue repositoryIssue, Date since, int page) {
        String organisation = repositoryIssue.getOwnerUsername();
        String repository = repositoryIssue.getRepositoryName();
        int issueNumber = repositoryIssue.getIssueNumber();
        String date = dateConverter.toISO8601NoMillisOrNull(since);
        return issueService.getCommentsFor(organisation, repository, issueNumber, date, page, DEFAULT_PER_PAGE_COUNT)
                .flatMapIterable(Response::body)
                .map(comment -> new RepositoryIssueEventComment(repositoryIssue, comment))
                .compose(PersistEventUserTransformer.newInstance(userDataLayer, eventUserConverter))
                .compose(PersistEventTransformer.newInstance(eventDataLayer, eventConverter));
    }

}
