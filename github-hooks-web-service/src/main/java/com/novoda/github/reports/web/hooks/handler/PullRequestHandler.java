package com.novoda.github.reports.web.hooks.handler;

import com.novoda.github.reports.data.db.ConnectionManager;
import com.novoda.github.reports.data.db.DbEventDataLayer;
import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.web.hooks.classification.EventType;
import com.novoda.github.reports.web.hooks.extract.ExtractException;
import com.novoda.github.reports.web.hooks.extract.PullRequestExtractor;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;

class PullRequestHandler implements EventHandler {

    private PullRequestExtractor extractor;
    private DbEventDataLayer eventDataLayer;
    // TODO we need a converter to convert from github issue to the db equivalent pojo (RepositoryIssueEvent?)
    // check:
    // - com.novoda.github.reports.lambda.issue.EventsServiceClient#retrieveEventsFrom()
    // - com.novoda.github.reports.lambda.issue.TransformToRepositoryIssueEvent


    static PullRequestHandler newInstance(ConnectionManager connectionManager) {
        PullRequestExtractor pullRequestExtractor = new PullRequestExtractor();
        DbEventDataLayer eventDataLayer = DbEventDataLayer.newInstance(connectionManager);
        return new PullRequestHandler(pullRequestExtractor, eventDataLayer);
    }

    PullRequestHandler(PullRequestExtractor extractor, DbEventDataLayer eventDataLayer) {
        this.extractor = extractor;
        this.eventDataLayer = eventDataLayer;
    }

    @Override
    public void handle(GithubWebhookEvent event) throws UnhandledEventException {

        GithubWebhookEvent.Action action = event.action();
        try {
            GithubIssue pullRequest = extractor.extractFrom(event);
        } catch (ExtractException e) {
            throw new UnhandledEventException(e.getMessage());
        }
        // TODO convert and persist, taking into account the value of 'action'

    }

    @Override
    public EventType handledEventType() {
        return EventType.PULL_REQUEST;
    }

}
