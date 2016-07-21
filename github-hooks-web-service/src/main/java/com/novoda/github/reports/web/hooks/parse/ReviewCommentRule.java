package com.novoda.github.reports.web.hooks.parse;

import com.novoda.github.reports.web.hooks.lambda.GithubWebhookEvent;

public class ReviewCommentRule implements ClassificationRule {
    @Override
    public boolean checkFor(GithubWebhookEvent event) {
        return event.comment() != null && event.pullRequest() != null;
    }
}
