package com.novoda.github.reports.web.hooks.handler;

import com.novoda.github.reports.web.hooks.classification.EventType;
import com.novoda.github.reports.web.hooks.extract.ExtractException;
import com.novoda.github.reports.web.hooks.extract.ReviewCommentExtractor;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;
import com.novoda.github.reports.web.hooks.model.ReviewComment;

public class ReviewCommentHandler implements EventHandler {

    private final ReviewCommentExtractor extractor;

    public static ReviewCommentHandler newInstance() {
        ReviewCommentExtractor extractor = new ReviewCommentExtractor();
        return new ReviewCommentHandler(extractor);
    }

    ReviewCommentHandler(ReviewCommentExtractor extractor) {
        this.extractor = extractor;
    }

    @Override
    public void handle(GithubWebhookEvent event) throws UnhandledEventException {

        GithubWebhookEvent.Action action = event.action();
        try {
            ReviewComment reviewComment = extractor.extractFrom(event);
        } catch (ExtractException e) {
            throw new UnhandledEventException(e.getMessage());
        }
        // TODO convert and persist, taking into account the value of 'action'
    }

    @Override
    public EventType handledEventType() {
        return EventType.REVIEW_COMMENT;
    }
}
