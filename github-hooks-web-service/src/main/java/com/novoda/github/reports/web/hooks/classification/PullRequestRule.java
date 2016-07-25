package com.novoda.github.reports.web.hooks.classification;

import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;

public class PullRequestRule implements ClassificationRule {
    @Override
    public boolean check(GithubWebhookEvent event) {
        return event.pullRequest() != null && event.comment() == null;
    }
}
