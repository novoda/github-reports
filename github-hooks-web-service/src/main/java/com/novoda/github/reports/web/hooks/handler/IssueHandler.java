package com.novoda.github.reports.web.hooks.handler;

import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.web.hooks.classification.EventType;
import com.novoda.github.reports.web.hooks.extract.ExtractException;
import com.novoda.github.reports.web.hooks.extract.IssueExtractor;
import com.novoda.github.reports.web.hooks.model.GithubAction;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;

public class IssueHandler implements EventHandler {

    private final IssueExtractor extractor;

    public static IssueHandler newInstance() {
        IssueExtractor issueExtractor = new IssueExtractor();
        return new IssueHandler(issueExtractor);
    }

    IssueHandler(IssueExtractor extractor) {
        this.extractor = extractor;
    }

    @Override
    public void handle(GithubWebhookEvent event) throws UnhandledEventException {

        GithubAction action = event.action();
        try {
            GithubIssue issue = extractor.extractFrom(event);
        } catch (ExtractException e) {
            throw new UnhandledEventException(e.getMessage());
        }
        // TODO convert and persist, taking into account the value of 'action'
    }

    @Override
    public EventType handledEventType() {
        return EventType.ISSUE;
    }
}
