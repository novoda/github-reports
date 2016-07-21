package com.novoda.github.reports.web.hooks.lambda;

import com.novoda.github.reports.web.hooks.EventType;

public class WebhookEventClassifier {

    // @RUI how can we clean this up?
    // - extract to methods
    // - have different classifiers (one for each type)?

    public EventType classify(GithubWebhookEvent event) {
        if (event.comment() == null) {
            if (event.issue() != null) {
                return EventType.ISSUE;
            } else if (event.pullRequest() != null) {
                return EventType.PULL_REQUEST;
            }
        } else {
            if (event.issue() != null) {
                return EventType.ISSUE_COMMENT;
            } else if (event.pullRequest() != null) {
                return EventType.REVIEW_COMMENT;
            }
             return EventType.COMMIT_COMMENT;
        }
        throw new IllegalStateException("Unknown event type.");
    }
}
