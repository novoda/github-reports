package com.novoda.github.reports.web.hooks.classification;

import com.novoda.github.reports.web.hooks.lambda.GithubWebhookEvent;

class CommitCommentRule implements ClassificationRule {
    @Override
    public boolean check(GithubWebhookEvent event) {
        return event.comment() != null
                && event.issue() == null
                && event.pullRequest() == null;
    }
}
