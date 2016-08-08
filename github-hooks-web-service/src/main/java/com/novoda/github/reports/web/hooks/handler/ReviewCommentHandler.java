package com.novoda.github.reports.web.hooks.handler;

import com.novoda.github.reports.data.db.ConnectionManager;
import com.novoda.github.reports.web.hooks.classification.EventType;
import com.novoda.github.reports.web.hooks.extract.ReviewCommentExtractor;
import com.novoda.github.reports.web.hooks.extract.ExtractException;
import com.novoda.github.reports.web.hooks.extract.PayloadExtractor;
import com.novoda.github.reports.web.hooks.model.ReviewComment;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;
import com.novoda.github.reports.web.hooks.persistence.ReviewCommentPersister;
import com.novoda.github.reports.web.hooks.persistence.PersistenceException;
import com.novoda.github.reports.web.hooks.persistence.Persister;

class ReviewCommentHandler implements EventHandler {

    private final PayloadExtractor<ReviewComment> extractor;
    private final Persister<ReviewComment> persister;

    static ReviewCommentHandler newInstance(ConnectionManager connectionManager) {
        PayloadExtractor<ReviewComment> extractor = new ReviewCommentExtractor();
        Persister<ReviewComment> persister = ReviewCommentPersister.newInstance(connectionManager);
        return new ReviewCommentHandler(extractor, persister);
    }

    private ReviewCommentHandler(PayloadExtractor<ReviewComment> extractor, Persister<ReviewComment> persister) {
        this.extractor = extractor;
        this.persister = persister;
    }

    @Override
    public void handle(GithubWebhookEvent event) throws UnhandledEventException {
        ReviewComment reviewComment = extractReviewComment(event);
        persist(reviewComment);
    }

    private ReviewComment extractReviewComment(GithubWebhookEvent event) throws UnhandledEventException {
        try {
            return extractor.extractFrom(event);
        } catch (ExtractException e) {
            throw new UnhandledEventException(e);
        }
    }

    private void persist(ReviewComment reviewComment) throws UnhandledEventException {
        try {
            persister.persist(reviewComment);
        } catch (PersistenceException e) {
            throw new UnhandledEventException(e);
        }
    }

    @Override
    public EventType handledEventType() {
        return EventType.REVIEW_COMMENT;
    }
}
