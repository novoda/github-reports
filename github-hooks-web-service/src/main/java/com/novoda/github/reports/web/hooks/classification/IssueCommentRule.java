package com.novoda.github.reports.web.hooks.classification;

import com.novoda.github.reports.web.hooks.lambda.GithubWebhookEvent;

public class IssueCommentRule implements ClassificationRule {
    @Override
    public boolean check(GithubWebhookEvent event) {
        return event.issue() != null && event.comment() != null;
    }
}
