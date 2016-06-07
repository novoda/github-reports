package com.novoda.github.reports.batch.issue;

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
import com.novoda.github.reports.batch.pullrequest.GithubPullRequestService;
import com.novoda.github.reports.batch.pullrequest.PullRequestService;
import com.novoda.github.reports.batch.repository.GithubRepository;
import com.novoda.github.reports.batch.retry.RateLimitResetTimerSubject;
import com.novoda.github.reports.batch.retry.RateLimitResetTimerSubjectContainer;
import com.novoda.github.reports.batch.retry.RetryWhenTokenResets;
import com.novoda.github.reports.data.EventDataLayer;
import com.novoda.github.reports.data.UserDataLayer;
import com.novoda.github.reports.data.db.ConnectionManager;
import com.novoda.github.reports.data.db.DbEventDataLayer;
import com.novoda.github.reports.data.db.DbUserDataLayer;
import com.novoda.github.reports.data.model.DatabaseEvent;
import com.novoda.github.reports.data.model.DatabaseUser;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import rx.Observable;
import rx.schedulers.Schedulers;

import static com.novoda.github.reports.batch.issue.GithubEvent.Type.*;

public class IssuesServiceClient {

    private static final Set<GithubEvent.Type> EVENT_TYPES_TO_BE_STORED = new HashSet<>(Arrays.asList(
            COMMENTED,
            CLOSED,
            HEAD_REF_DELETED,
            LABELED,
            MERGED,
            UNLABELED
    ));

    private final IssueService issueService;
    private final PullRequestService pullRequestService;
    private final EventDataLayer eventDataLayer;
    private final Converter<RepositoryIssue, DatabaseEvent> issueConverter;
    private final UserDataLayer userDataLayer;
    private final Converter<RepositoryIssue, DatabaseUser> userConverter;
    private final Converter<RepositoryIssueEvent, DatabaseUser> eventUserConverter;
    private final Converter<RepositoryIssueEvent, DatabaseEvent> eventConverter;

    private final RateLimitResetTimerSubject rateLimitResetTimerSubject;

    public static IssuesServiceClient newInstance() {
        IssueService issueService = GithubIssueService.newInstance();
        PullRequestService pullRequestService = GithubPullRequestService.newInstance();
        ConnectionManager connectionManager = ConnectionManagerContainer.getConnectionManager();

        EventDataLayer eventDataLayer = DbEventDataLayer.newInstance(connectionManager);
        Converter<RepositoryIssue, DatabaseEvent> issueConverter = IssueConverter.newInstance();
        UserDataLayer userDataLayer = DbUserDataLayer.newInstance(connectionManager);
        Converter<RepositoryIssue, DatabaseUser> userConverter = UserConverter.newInstance();
        Converter<RepositoryIssueEvent, DatabaseUser> userEventConverter = EventUserConverter.newInstance();
        Converter<RepositoryIssueEvent, DatabaseEvent> eventConverter = EventConverter.newInstance();

        RateLimitResetTimerSubject rateLimitResetTimerSubject = RateLimitResetTimerSubjectContainer.getInstance();

        return new IssuesServiceClient(
                issueService,
                pullRequestService,
                eventDataLayer,
                issueConverter,
                userDataLayer,
                userConverter,
                userEventConverter,
                eventConverter,
                rateLimitResetTimerSubject
        );
    }

    private IssuesServiceClient(IssueService issueService,
                                PullRequestService pullRequestService,
                                EventDataLayer eventDataLayer,
                                Converter<RepositoryIssue, DatabaseEvent> issueConverter,
                                UserDataLayer userDataLayer,
                                Converter<RepositoryIssue, DatabaseUser> userConverter,
                                Converter<RepositoryIssueEvent, DatabaseUser> eventUserConverter,
                                Converter<RepositoryIssueEvent, DatabaseEvent> eventConverter,
                                RateLimitResetTimerSubject rateLimitResetTimerSubject) {
        this.issueService = issueService;
        this.pullRequestService = pullRequestService;
        this.eventDataLayer = eventDataLayer;
        this.issueConverter = issueConverter;
        this.userDataLayer = userDataLayer;
        this.userConverter = userConverter;
        this.eventUserConverter = eventUserConverter;
        this.eventConverter = eventConverter;
        this.rateLimitResetTimerSubject = rateLimitResetTimerSubject;
    }

    public Observable<RepositoryIssue> retrieveIssuesFrom(GithubRepository repository, Date since) {
        return issueService.getIssuesFor(repository.getOwner().getUsername(), repository.getName(), since)
                .compose(RetryWhenTokenResets.newInstance(rateLimitResetTimerSubject))
                .map(issue -> RepositoryIssue.newInstance(repository, issue))
                .compose(PersistUserTransformer.newInstance(userDataLayer, userConverter))
                .compose(PersistIssueTransformer.newInstance(eventDataLayer, issueConverter))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.immediate());
    }

    public Observable<RepositoryIssueEvent> retrieveCommentsFrom(RepositoryIssue repositoryIssue, Date since) {
        return Observable
                .merge(
                        retrieveCommentsFromIssue(repositoryIssue, since),
                        retrieveCommentsFromPullRequestReview(repositoryIssue, since)
                )
                .map(comment -> RepositoryIssueEventComment.newInstance(repositoryIssue, comment))
                .compose(PersistEventUserTransformer.newInstance(userDataLayer, eventUserConverter))
                .compose(PersistEventTransformer.newInstance(eventDataLayer, eventConverter))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.immediate());
    }

    private Observable<GithubComment> retrieveCommentsFromIssue(RepositoryIssue repositoryIssue, Date since) {
        String organisation = repositoryIssue.getRepository().getOwner().getUsername();
        String repository = repositoryIssue.getRepository().getName();
        int issueNumber = repositoryIssue.getIssue().getNumber();
        return issueService
                .getCommentsFor(organisation, repository, issueNumber, since)
                .compose(RetryWhenTokenResets.newInstance(rateLimitResetTimerSubject));
    }

    private Observable<GithubComment> retrieveCommentsFromPullRequestReview(RepositoryIssue repositoryIssue, Date since) {
        if (!repositoryIssue.getIssue().isPullRequest()) {
            return Observable.empty();
        }
        String organisation = repositoryIssue.getRepository().getOwner().getUsername();
        String repository = repositoryIssue.getRepository().getName();
        int issueNumber = repositoryIssue.getIssue().getNumber();
        return pullRequestService
                .getReviewCommentsForPullRequestFor(organisation, repository, issueNumber, since)
                .compose(RetryWhenTokenResets.newInstance(rateLimitResetTimerSubject));
    }

    public Observable<RepositoryIssueEvent> retrieveEventsFrom(RepositoryIssue repositoryIssue, Date since) {
        return issueService
                .getEventsFor(
                        repositoryIssue.getRepository().getOwner().getUsername(),
                        repositoryIssue.getRepository().getName(),
                        repositoryIssue.getIssue().getNumber(),
                        since
                )
                .compose(RetryWhenTokenResets.newInstance(rateLimitResetTimerSubject))
                .filter(this::shouldStoreEvent)
                .map(event -> RepositoryIssueEventEvent.newInstance(repositoryIssue, event))
                .compose(PersistEventUserTransformer.newInstance(userDataLayer, eventUserConverter))
                .compose(PersistEventTransformer.newInstance(eventDataLayer, eventConverter))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.immediate());
    }

    private boolean shouldStoreEvent(GithubEvent event) {
        return EVENT_TYPES_TO_BE_STORED.contains(event.getType());
    }

}
