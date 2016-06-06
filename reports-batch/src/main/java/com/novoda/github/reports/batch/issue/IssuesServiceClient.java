package com.novoda.github.reports.batch.issue;

import com.novoda.github.reports.batch.network.RateLimitRemainingResetRepositoryContainer;
import com.novoda.github.reports.batch.network.RateLimitResetRepository;
import com.novoda.github.reports.batch.persistence.ConnectionManagerContainer;
import com.novoda.github.reports.batch.persistence.EventUserConverter;
import com.novoda.github.reports.batch.persistence.PersistEventTransformer;
import com.novoda.github.reports.batch.persistence.PersistEventUserTransformer;
import com.novoda.github.reports.batch.persistence.PersistIssueTransformer;
import com.novoda.github.reports.batch.persistence.PersistUserTransformer;
import com.novoda.github.reports.batch.persistence.converter.Converter;
import com.novoda.github.reports.batch.persistence.converter.EventConverter;
import com.novoda.github.reports.batch.persistence.converter.IssueConverter;
import com.novoda.github.reports.batch.persistence.converter.UserConverter;
import com.novoda.github.reports.batch.repository.Repository;
import com.novoda.github.reports.batch.retry.RateLimitResetTimerSubject;
import com.novoda.github.reports.batch.retry.RateLimitResetTimerSubjectContainer;
import com.novoda.github.reports.batch.retry.RetryWhenTokenResets;
import com.novoda.github.reports.data.EventDataLayer;
import com.novoda.github.reports.data.UserDataLayer;
import com.novoda.github.reports.data.db.ConnectionManager;
import com.novoda.github.reports.data.db.DbEventDataLayer;
import com.novoda.github.reports.data.db.DbUserDataLayer;
import com.novoda.github.reports.data.model.Event;
import com.novoda.github.reports.data.model.User;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import rx.Observable;
import rx.schedulers.Schedulers;

import static com.novoda.github.reports.batch.issue.Event.Type.*;

public class IssuesServiceClient {

    private final Set<com.novoda.github.reports.batch.issue.Event.Type> typeSet = new HashSet<>(Arrays.asList(
            COMMENTED,
            CLOSED,
            HEAD_REF_DELETED,
            LABELED,
            MERGED,
            UNLABELED
    ));

    private final IssueService issueService;
    private final EventDataLayer eventDataLayer;
    private final Converter<RepositoryIssue, com.novoda.github.reports.data.model.Event> issueConverter;
    private final UserDataLayer userDataLayer;
    private final Converter<RepositoryIssue, com.novoda.github.reports.data.model.User> userConverter;
    private final Converter<RepositoryIssueEvent, com.novoda.github.reports.data.model.User> eventUserConverter;
    private final Converter<RepositoryIssueEvent, com.novoda.github.reports.data.model.Event> eventConverter;

    private final RateLimitResetRepository rateLimitResetRepository;
    private final RateLimitResetTimerSubject rateLimitResetTimerSubject;

    public static IssuesServiceClient newInstance() {
        IssueService issueService = GithubIssueService.newInstance();
        ConnectionManager connectionManager = ConnectionManagerContainer.getConnectionManager();

        EventDataLayer eventDataLayer = DbEventDataLayer.newInstance(connectionManager);
        Converter<RepositoryIssue, com.novoda.github.reports.data.model.Event> issueConverter = IssueConverter.newInstance();
        UserDataLayer userDataLayer = DbUserDataLayer.newInstance(connectionManager);
        Converter<RepositoryIssue, com.novoda.github.reports.data.model.User> userConverter = UserConverter.newInstance();
        Converter<RepositoryIssueEvent, com.novoda.github.reports.data.model.User> userEventConverter = EventUserConverter.newInstance();
        Converter<RepositoryIssueEvent, com.novoda.github.reports.data.model.Event> eventConverter = EventConverter.newInstance();

        RateLimitResetRepository rateLimitResetRepository = RateLimitRemainingResetRepositoryContainer.getInstance();
        RateLimitResetTimerSubject rateLimitResetTimerSubject = RateLimitResetTimerSubjectContainer.getInstance();

        return new IssuesServiceClient(
                issueService,
                eventDataLayer,
                issueConverter,
                userDataLayer,
                userConverter,
                userEventConverter,
                eventConverter,
                rateLimitResetRepository, rateLimitResetTimerSubject
        );
    }

    private IssuesServiceClient(IssueService issueService,
                                EventDataLayer eventDataLayer,
                                Converter<RepositoryIssue, Event> issueConverter,
                                UserDataLayer userDataLayer,
                                Converter<RepositoryIssue, User> userConverter,
                                Converter<RepositoryIssueEvent, User> eventUserConverter,
                                Converter<RepositoryIssueEvent, Event> eventConverter,
                                RateLimitResetRepository rateLimitResetRepository,
                                RateLimitResetTimerSubject rateLimitResetTimerSubject) {
        this.issueService = issueService;
        this.eventDataLayer = eventDataLayer;
        this.issueConverter = issueConverter;
        this.userDataLayer = userDataLayer;
        this.userConverter = userConverter;
        this.eventUserConverter = eventUserConverter;
        this.eventConverter = eventConverter;
        this.rateLimitResetRepository = rateLimitResetRepository;
        this.rateLimitResetTimerSubject = rateLimitResetTimerSubject;
    }

    public Observable<RepositoryIssue> retrieveIssuesFrom(Repository repository, Date since) {
        return issueService.getPagedIssuesFor(repository.getOwner().getUsername(), repository.getName(), since)
                .compose(RetryWhenTokenResets.newInstance(rateLimitResetTimerSubject, rateLimitResetRepository))
                .map(issue -> RepositoryIssue.newInstance(repository, issue))
                .compose(PersistUserTransformer.newInstance(userDataLayer, userConverter))
                .compose(PersistIssueTransformer.newInstance(eventDataLayer, issueConverter))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.immediate());
    }

    public Observable<RepositoryIssueEvent> retrieveCommentsFrom(RepositoryIssue repositoryIssue, Date since) {
        return issueService
                .getPagedCommentsFor(
                        repositoryIssue.getRepository().getOwner().getUsername(),
                        repositoryIssue.getRepository().getName(),
                        repositoryIssue.getIssue().getNumber(),
                        since
                )
                .compose(RetryWhenTokenResets.newInstance(rateLimitResetTimerSubject, rateLimitResetRepository))
                .map(comment -> RepositoryIssueEventComment.newInstance(repositoryIssue, comment))
                .compose(PersistEventUserTransformer.newInstance(userDataLayer, eventUserConverter))
                .compose(PersistEventTransformer.newInstance(eventDataLayer, eventConverter))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.immediate());
    }

    public Observable<RepositoryIssueEvent> retrieveEventsFrom(RepositoryIssue repositoryIssue) {
        return issueService
                .getPagedEventsFor(
                        repositoryIssue.getRepository().getOwner().getUsername(),
                        repositoryIssue.getRepository().getName(),
                        repositoryIssue.getIssue().getNumber()
                )
                .compose(RetryWhenTokenResets.newInstance(rateLimitResetTimerSubject, rateLimitResetRepository))
                .filter(this::isInterestingEvent)
                .map(event -> RepositoryIssueEventEvent.newInstance(repositoryIssue, event))
                .compose(PersistEventUserTransformer.newInstance(userDataLayer, eventUserConverter))
                .compose(PersistEventTransformer.newInstance(eventDataLayer, eventConverter))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.immediate());
    }

    private boolean isInterestingEvent(com.novoda.github.reports.batch.issue.Event event) {
        return typeSet.contains(event.getType());
    }

}
