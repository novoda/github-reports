package com.novoda.github.reports.web.hooks.handler;

import com.novoda.github.reports.data.DataLayerException;
import com.novoda.github.reports.data.db.ConnectionManager;
import com.novoda.github.reports.data.db.DbEventDataLayer;
import com.novoda.github.reports.data.db.DbUserDataLayer;
import com.novoda.github.reports.data.model.Event;
import com.novoda.github.reports.data.model.User;
import com.novoda.github.reports.service.issue.GithubEvent;
import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.service.repository.GithubRepository;
import com.novoda.github.reports.web.hooks.classification.EventType;
import com.novoda.github.reports.web.hooks.extract.ExtractException;
import com.novoda.github.reports.web.hooks.extract.PullRequestExtractor;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;
import com.novoda.github.reports.web.hooks.model.PullRequest;

class PullRequestHandler implements EventHandler {

    private PullRequestExtractor extractor;
    private DbEventDataLayer eventDataLayer;
    private DbUserDataLayer userDataLayer;

    // TODO we need a converter to convert from github issue to the db equivalent pojo (RepositoryIssueEvent?)
    // check:
    // - com.novoda.github.reports.lambda.issue.EventsServiceClient#retrieveEventsFrom()
    // - com.novoda.github.reports.lambda.issue.TransformToRepositoryIssueEvent
    // - RepositoryIssueEventPersistTransformer, uses:
    //      - PersistEventUserTransformer   (composing on an observable of RepositoryIssueEvent)
    //      - PersistEventTransformer       (composing on an observable of RepositoryIssueEvent)
    //      . each of these two has an operator (PersistEventUserOperator and PersistEventsOperator) that hold the respective DataLayers
    //        and converters:
    //          .. PersistEventUserOperator: DataLayer<User> dataLayer, Converter<RepositoryIssueEvent, User> converter
    //          .. PersistEventsOperator: DataLayer<Event> dataLayer, Converter<RepositoryIssueEvent, Event> converter
    //         ... each PersistOperator: dataLayer.updateOrInsert(converter.convertListFrom(elements));



    static PullRequestHandler newInstance(ConnectionManager connectionManager) {
        PullRequestExtractor pullRequestExtractor = new PullRequestExtractor();
        DbEventDataLayer eventDataLayer = DbEventDataLayer.newInstance(connectionManager);
        DbUserDataLayer userDataLayer = DbUserDataLayer.newInstance(connectionManager);
        return new PullRequestHandler(pullRequestExtractor, eventDataLayer, userDataLayer);
    }

    PullRequestHandler(PullRequestExtractor extractor, DbEventDataLayer eventDataLayer, DbUserDataLayer userDataLayer) {
        this.extractor = extractor;
        this.eventDataLayer = eventDataLayer;
        this.userDataLayer = userDataLayer;
    }

    @Override
    public void handle(GithubWebhookEvent event) throws UnhandledEventException {

        // TODO convert and persist, taking into account the value of 'action'

        GithubWebhookEvent.Action action = event.action();
        try {
            PullRequest pullRequest = extractor.extractFrom(event);
            GithubIssue issue = pullRequest.getIssue();
            GithubRepository repository = pullRequest.getRepository();

            // TODO convert action to GithubEvent.Type
            // TODO convert from GithubIssue to GithubEvent
            GithubEvent githubEvent = new GithubEvent(
                    issue.getId(),
                    issue.getUser(),
                    GithubEvent.Type.ASSIGNED,
                    issue.getCreatedAt()
            );

            Event dbEvent = Event.create(
                    issue.getId(),
                    repository.getId(),
                    issue.getUserId(),
                    issue.getUserId(),
                    com.novoda.github.reports.data.model.EventType.BRANCH_DELETE,
                    issue.getUpdatedAt()
            );
            User dbUser = User.create(issue.getUserId(), issue.getUser().getUsername());

            try {
                eventDataLayer.updateOrInsert(dbEvent);
                userDataLayer.updateOrInsert(dbUser);
            } catch (DataLayerException e) {
                e.printStackTrace();
                throw new UnhandledEventException(e.getMessage());
            }

        } catch (ExtractException e) {
            throw new UnhandledEventException(e.getMessage());
        }

    }

    @Override
    public EventType handledEventType() {
        return EventType.PULL_REQUEST;
    }

}
