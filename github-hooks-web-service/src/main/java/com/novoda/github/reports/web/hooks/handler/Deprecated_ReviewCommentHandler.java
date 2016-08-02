package com.novoda.github.reports.web.hooks.handler;

import com.novoda.github.reports.web.hooks.classification.EventType;
import com.novoda.github.reports.web.hooks.extract.ExtractException;
import com.novoda.github.reports.web.hooks.extract.Deprecated_ReviewCommentExtractor;
import com.novoda.github.reports.web.hooks.model.GithubAction;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;
import com.novoda.github.reports.web.hooks.model.Deprecated_ReviewComment;

@Deprecated
public class Deprecated_ReviewCommentHandler implements EventHandler {

    private final Deprecated_ReviewCommentExtractor extractor;

    public static Deprecated_ReviewCommentHandler newInstance() {
        Deprecated_ReviewCommentExtractor extractor = new Deprecated_ReviewCommentExtractor();
        return new Deprecated_ReviewCommentHandler(extractor);
    }

    Deprecated_ReviewCommentHandler(Deprecated_ReviewCommentExtractor extractor) {
        this.extractor = extractor;
    }

    @Override
    public void handle(GithubWebhookEvent event) throws UnhandledEventException {

        GithubAction action = event.action();
        try {
            Deprecated_ReviewComment deprecatedReviewComment = extractor.extractFrom(event);
        } catch (ExtractException e) {
            throw new UnhandledEventException(e.getMessage());
        }
        // TODO convert and persist, taking into account the value of 'action'
    }

    @Override
    public EventType handledEventType() {
        return EventType.DEPRECATED_REVIEW_COMMENT;
    }
}
