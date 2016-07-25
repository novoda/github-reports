package com.novoda.github.reports.web.hooks.classification;

import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;

public class ReviewCommentRule implements ClassificationRule {
    @Override
    public boolean check(GithubWebhookEvent event) {
        return event.comment() != null && event.pullRequest() != null;
    }
}
