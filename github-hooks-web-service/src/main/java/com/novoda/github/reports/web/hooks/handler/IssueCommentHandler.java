package com.novoda.github.reports.web.hooks.handler;

import com.novoda.github.reports.data.db.ConnectionManager;
import com.novoda.github.reports.web.hooks.classification.EventType;
import com.novoda.github.reports.web.hooks.extract.ExtractException;
import com.novoda.github.reports.web.hooks.extract.IssueCommentExtractor;
import com.novoda.github.reports.web.hooks.extract.PayloadExtractor;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;
import com.novoda.github.reports.web.hooks.model.IssueComment;
import com.novoda.github.reports.web.hooks.persistence.IssueCommentPersister;
import com.novoda.github.reports.web.hooks.persistence.PersistenceException;
import com.novoda.github.reports.web.hooks.persistence.Persister;

class IssueCommentHandler implements EventHandler {

    private final PayloadExtractor<IssueComment> extractor;
    private final Persister<IssueComment> persister;

    static IssueCommentHandler newInstance(ConnectionManager connectionManager) {
        PayloadExtractor<IssueComment> extractor = new IssueCommentExtractor();
        Persister<IssueComment> persister = IssueCommentPersister.newInstance(connectionManager);
        return new IssueCommentHandler(extractor, persister);
    }

    private IssueCommentHandler(PayloadExtractor<IssueComment> extractor, Persister<IssueComment> persister) {
        this.extractor = extractor;
        this.persister = persister;
    }

    @Override
    public void handle(GithubWebhookEvent event) throws UnhandledEventException {
        IssueComment issueComment = extractIssueComment(event);
        persist(issueComment);
    }

    private IssueComment extractIssueComment(GithubWebhookEvent event) throws UnhandledEventException {
        try {
            return extractor.extractFrom(event);
        } catch (ExtractException e) {
            throw new UnhandledEventException(e.getMessage());
        }
    }

    private void persist(IssueComment issueComment) throws UnhandledEventException {
        try {
            persister.persist(issueComment);
        } catch (PersistenceException e) {
            throw new UnhandledEventException(e.getMessage());
        }
    }

    @Override
    public EventType handledEventType() {
        return EventType.ISSUE_COMMENT;
    }
}
