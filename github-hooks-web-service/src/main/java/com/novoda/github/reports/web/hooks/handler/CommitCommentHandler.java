package com.novoda.github.reports.web.hooks.handler;

import com.novoda.github.reports.service.issue.GithubComment;
import com.novoda.github.reports.web.hooks.classification.EventType;
import com.novoda.github.reports.web.hooks.extract.CommitCommentExtractor;
import com.novoda.github.reports.web.hooks.extract.ExtractException;
import com.novoda.github.reports.web.hooks.model.GithubAction;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;

public class CommitCommentHandler implements EventHandler {

    private final CommitCommentExtractor extractor;

    public static CommitCommentHandler newInstance() {
        CommitCommentExtractor extractor = new CommitCommentExtractor();
        return new CommitCommentHandler(extractor);
    }

    CommitCommentHandler(CommitCommentExtractor extractor) {
        this.extractor = extractor;
    }

    @Override
    public void handle(GithubWebhookEvent event) throws UnhandledEventException {

        GithubAction action = event.action();
        try {
            GithubComment comment = extractor.extractFrom(event);
        } catch (ExtractException e) {
            throw new UnhandledEventException(e.getMessage());
        }
        // TODO convert and persist, taking into account the value of 'action'
    }

    @Override
    public EventType handledEventType() {
        return EventType.COMMIT_COMMENT;
    }
}
