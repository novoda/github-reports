package com.novoda.github.reports.web.hooks.parse;

import com.novoda.github.reports.web.hooks.lambda.GithubWebhookEvent;

public class PullRequestRule implements ClassificationRule {
    @Override
    public boolean checkFor(GithubWebhookEvent event) {
        return event.pullRequest() != null && event.comment() == null;
    }
}
