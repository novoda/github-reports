package com.novoda.github.reports.web.hooks.handler;

import com.novoda.github.reports.data.db.ConnectionManager;
import com.novoda.github.reports.web.hooks.classification.EventType;
import com.novoda.github.reports.web.hooks.extract.CommitCommentExtractor;
import com.novoda.github.reports.web.hooks.extract.ExtractException;
import com.novoda.github.reports.web.hooks.extract.PayloadExtractor;
import com.novoda.github.reports.web.hooks.model.CommitComment;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;
import com.novoda.github.reports.web.hooks.persistence.CommitCommentPersister;
import com.novoda.github.reports.web.hooks.persistence.PersistenceException;
import com.novoda.github.reports.web.hooks.persistence.Persister;

class CommitCommentHandler implements EventHandler {

    private final PayloadExtractor<CommitComment> extractor;
    private final Persister<CommitComment> persister;

    static CommitCommentHandler newInstance(ConnectionManager connectionManager) {
        PayloadExtractor<CommitComment> extractor = new CommitCommentExtractor();
        Persister<CommitComment> persister = CommitCommentPersister.newInstance(connectionManager);
        return new CommitCommentHandler(extractor, persister);
    }

    private CommitCommentHandler(PayloadExtractor<CommitComment> extractor, Persister<CommitComment> persister) {
        this.extractor = extractor;
        this.persister = persister;
    }

    @Override
    public void handle(GithubWebhookEvent event) throws UnhandledEventException {
        CommitComment reviewComment = extractCommitComment(event);
        persist(reviewComment);
    }

    private CommitComment extractCommitComment(GithubWebhookEvent event) throws UnhandledEventException {
        try {
            return extractor.extractFrom(event);
        } catch (ExtractException e) {
            throw new UnhandledEventException(e);
        }
    }

    private void persist(CommitComment reviewComment) throws UnhandledEventException {
        try {
            persister.persist(reviewComment);
        } catch (PersistenceException e) {
            throw new UnhandledEventException(e);
        }
    }

    @Override
    public EventType handledEventType() {
        return EventType.COMMIT_COMMENT;
    }
}
