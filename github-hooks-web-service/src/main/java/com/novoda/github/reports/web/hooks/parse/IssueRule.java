package com.novoda.github.reports.web.hooks.parse;

import com.novoda.github.reports.web.hooks.lambda.GithubWebhookEvent;

public class IssueRule implements ClassificationRule {
    @Override
    public boolean check(GithubWebhookEvent event) {
        return event.issue() != null && event.comment() == null;
    }
}
