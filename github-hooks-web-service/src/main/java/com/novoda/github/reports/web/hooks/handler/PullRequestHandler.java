package com.novoda.github.reports.web.hooks.handler;

import com.novoda.github.reports.data.db.ConnectionManager;
import com.novoda.github.reports.web.hooks.classification.EventType;
import com.novoda.github.reports.web.hooks.extract.ExtractException;
import com.novoda.github.reports.web.hooks.extract.PayloadExtractor;
import com.novoda.github.reports.web.hooks.extract.PullRequestExtractor;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;
import com.novoda.github.reports.web.hooks.model.PullRequest;
import com.novoda.github.reports.web.hooks.persistence.PersistenceException;
import com.novoda.github.reports.web.hooks.persistence.Persister;
import com.novoda.github.reports.web.hooks.persistence.PullRequestPersister;

class PullRequestHandler implements EventHandler {

    private final PayloadExtractor<PullRequest> extractor;
    private final Persister<PullRequest> persister;

    static PullRequestHandler newInstance(ConnectionManager connectionManager) {
        PullRequestExtractor extractor = new PullRequestExtractor();
        Persister<PullRequest> persister = PullRequestPersister.newInstance(connectionManager);
        return new PullRequestHandler(extractor, persister);
    }

    PullRequestHandler(PayloadExtractor<PullRequest> extractor, Persister<PullRequest> persister) {
        this.extractor = extractor;
        this.persister = persister;
    }

    @Override
    public void handle(GithubWebhookEvent event) throws UnhandledEventException {
        PullRequest pullRequest = extractPullRequest(event);
        persist(pullRequest);
    }

    private PullRequest extractPullRequest(GithubWebhookEvent event) throws UnhandledEventException {
        try {
            return extractor.extractFrom(event);
        } catch (ExtractException e) {
            throw new UnhandledEventException(e.getMessage());
        }
    }

    private void persist(PullRequest pullRequest) throws UnhandledEventException {
        try {
            persister.persist(pullRequest);
        } catch (PersistenceException e) {
            throw new UnhandledEventException(e.getMessage());
        }
    }

    @Override
    public EventType handledEventType() {
        return EventType.PULL_REQUEST;
    }
}
