package com.novoda.github.reports.web.hooks.extract;

import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.web.hooks.lambda.GithubWebhookEvent;

class PullRequestExtractor implements PayloadExtractor<GithubIssue> {
    @Override
    public GithubIssue extractFrom(GithubWebhookEvent event) {
        GithubIssue pullRequest = event.pullRequest();
        if (pullRequest == null) {
            throw new IllegalStateException("Unable to get pull request from the event.");
        }
        pullRequest.setIsPullRequest(true);
        return pullRequest;
    }
}
