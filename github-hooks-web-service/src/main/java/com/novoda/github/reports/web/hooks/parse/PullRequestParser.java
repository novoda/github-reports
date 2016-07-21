package com.novoda.github.reports.web.hooks.parse;

import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.web.hooks.lambda.GithubWebhookEvent;

import java.util.Optional;

public class PullRequestParser implements WebhookEventParser<GithubIssue> {

    @Override
    public Optional<GithubIssue> from(GithubWebhookEvent event) {
        GithubIssue pullRequest = event.pullRequest();
        if (pullRequest == null) {
            return Optional.empty();
        }
        pullRequest.setIsPullRequest(true);
        return Optional.of(pullRequest);
    }
}
