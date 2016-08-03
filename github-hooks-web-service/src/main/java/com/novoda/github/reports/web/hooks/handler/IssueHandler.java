package com.novoda.github.reports.web.hooks.handler;

import com.novoda.github.reports.data.db.ConnectionManager;
import com.novoda.github.reports.web.hooks.classification.EventType;
import com.novoda.github.reports.web.hooks.extract.ExtractException;
import com.novoda.github.reports.web.hooks.extract.IssueExtractor;
import com.novoda.github.reports.web.hooks.extract.PayloadExtractor;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;
import com.novoda.github.reports.web.hooks.model.Issue;
import com.novoda.github.reports.web.hooks.persistence.IssuePersister;
import com.novoda.github.reports.web.hooks.persistence.PersistenceException;
import com.novoda.github.reports.web.hooks.persistence.Persister;

class IssueHandler implements EventHandler {

    private final PayloadExtractor<Issue> extractor;
    private final Persister<Issue> persister;

    static IssueHandler newInstance(ConnectionManager connectionManager) {
        PayloadExtractor<Issue> issueExtractor = new IssueExtractor();
        Persister<Issue> persister = IssuePersister.newInstance(connectionManager);
        return new IssueHandler(issueExtractor, persister);
    }

    private IssueHandler(PayloadExtractor<Issue> extractor, Persister<Issue> persister) {
        this.extractor = extractor;
        this.persister = persister;
    }

    @Override
    public void handle(GithubWebhookEvent event) throws UnhandledEventException {
        Issue issue = extractIssue(event);
        persist(issue);
    }

    private Issue extractIssue(GithubWebhookEvent event) throws UnhandledEventException {
        try {
            return extractor.extractFrom(event);
        } catch (ExtractException e) {
            throw new UnhandledEventException(e.getMessage());
        }
    }

    private void persist(Issue issue) throws UnhandledEventException {
        try {
            persister.persist(issue);
        } catch (PersistenceException e) {
            throw new UnhandledEventException(e.getMessage());
        }
    }

    @Override
    public EventType handledEventType() {
        return EventType.ISSUE;
    }
}
