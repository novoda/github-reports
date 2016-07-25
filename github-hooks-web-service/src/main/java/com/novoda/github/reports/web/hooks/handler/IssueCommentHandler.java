package com.novoda.github.reports.web.hooks.handler;

import com.novoda.github.reports.web.hooks.classification.EventType;
import com.novoda.github.reports.web.hooks.extract.ExtractException;
import com.novoda.github.reports.web.hooks.extract.IssueCommentExtractor;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;
import com.novoda.github.reports.web.hooks.model.IssueComment;

public class IssueCommentHandler implements EventHandler {

    private final IssueCommentExtractor extractor;

    public static IssueCommentHandler newInstance() {
        IssueCommentExtractor extractor = new IssueCommentExtractor();
        return new IssueCommentHandler(extractor);
    }

    IssueCommentHandler(IssueCommentExtractor extractor) {
        this.extractor = extractor;
    }

    @Override
    public void handle(GithubWebhookEvent event) throws UnhandledEventException {

        GithubWebhookEvent.Action action = event.action();
        try {
            IssueComment issueComment = extractor.extractFrom(event);
        } catch (ExtractException e) {
            throw new UnhandledEventException(e.getMessage());
        }
        // TODO convert and persist, taking into account the value of 'action'
    }

    @Override
    public EventType handledEventType() {
        return EventType.ISSUE_COMMENT;
    }
}
