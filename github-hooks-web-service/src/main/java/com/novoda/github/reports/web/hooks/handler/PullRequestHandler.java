package com.novoda.github.reports.web.hooks.handler;

import com.novoda.github.reports.data.db.ConnectionManager;
import com.novoda.github.reports.data.db.DbEventDataLayer;
import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.web.hooks.EventType;
import com.novoda.github.reports.web.hooks.extract.ExtractException;
import com.novoda.github.reports.web.hooks.extract.PullRequestExtractor;
import com.novoda.github.reports.web.hooks.lambda.GithubWebhookEvent;
import com.novoda.github.reports.web.hooks.classification.WebhookEventClassifier;

class PullRequestHandler implements EventHandler {

    private WebhookEventClassifier eventClassifier;
    private PullRequestExtractor extractor;
    private DbEventDataLayer eventDataLayer;

    static PullRequestHandler newInstance(ConnectionManager connectionManager) {
        WebhookEventClassifier eventClassifier = new WebhookEventClassifier();
        PullRequestExtractor pullRequestExtractor = new PullRequestExtractor();
        DbEventDataLayer eventDataLayer = DbEventDataLayer.newInstance(connectionManager);
        return new PullRequestHandler(eventClassifier, pullRequestExtractor, eventDataLayer);
    }

    PullRequestHandler(WebhookEventClassifier eventClassifier, PullRequestExtractor extractor, DbEventDataLayer eventDataLayer) {
        this.eventClassifier = eventClassifier;
        this.extractor = extractor;
        this.eventDataLayer = eventDataLayer;
    }

    @Override
    public boolean handle(GithubWebhookEvent event) throws UnhandledEventException {
        if (cannotHandleEvent(event)) {
            return false;
        }

        GithubWebhookEvent.Action action = event.action();
        try {
            GithubIssue pullRequest = extractor.extractFrom(event);
        } catch (ExtractException e) {
            throw new UnhandledEventException(e.getMessage());
        }
        // TODO convert and persist and... ?

        return true;
    }

    private boolean cannotHandleEvent(GithubWebhookEvent event) {
        return eventClassifier.classify(event) != EventType.PULL_REQUEST;
    }
}
