package com.novoda.github.reports.web.hooks.handler;

import com.novoda.github.reports.data.db.ConnectionManager;
import com.novoda.github.reports.web.hooks.classification.EventType;
import com.novoda.github.reports.web.hooks.extract.ExtractException;
import com.novoda.github.reports.web.hooks.extract.PayloadExtractor;
import com.novoda.github.reports.web.hooks.extract.PullRequestCommentExtractor;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;
import com.novoda.github.reports.web.hooks.model.PullRequestComment;
import com.novoda.github.reports.web.hooks.persistence.PersistenceException;
import com.novoda.github.reports.web.hooks.persistence.Persister;
import com.novoda.github.reports.web.hooks.persistence.PullRequestCommentPersister;

class PullRequestCommentHandler implements EventHandler {

    private final PayloadExtractor<PullRequestComment> extractor;
    private final Persister<PullRequestComment> persister;

    static PullRequestCommentHandler newInstance(ConnectionManager connectionManager) {
        PayloadExtractor<PullRequestComment> extractor = new PullRequestCommentExtractor();
        Persister<PullRequestComment> persister = PullRequestCommentPersister.newInstance(connectionManager);
        return new PullRequestCommentHandler(extractor, persister);
    }

    private PullRequestCommentHandler(PayloadExtractor<PullRequestComment> extractor,
                                      Persister<PullRequestComment> persister) {

        this.extractor = extractor;
        this.persister = persister;
    }

    @Override
    public void handle(GithubWebhookEvent event) throws UnhandledEventException {
        PullRequestComment pullRequestComment = extractPullRequestComment(event);
        persist(pullRequestComment);
    }

    private PullRequestComment extractPullRequestComment(GithubWebhookEvent event) throws UnhandledEventException {
        try {
            return extractor.extractFrom(event);
        } catch (ExtractException e) {
            throw new UnhandledEventException(e);
        }
    }

    private void persist(PullRequestComment pullRequestComment) throws UnhandledEventException {
        try {
            persister.persist(pullRequestComment);
        } catch (PersistenceException e) {
            throw new UnhandledEventException(e);
        }
    }

    @Override
    public EventType handledEventType() {
        return EventType.PULL_REQUEST_COMMENT;
    }
}
