package com.novoda.github.reports.web.hooks.extract;

import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.web.hooks.lambda.GithubWebhookEvent;

public class PullRequestExtractor implements PayloadExtractor<GithubIssue> {
    @Override
    public GithubIssue extractFrom(GithubWebhookEvent event) throws ExtractException {
        GithubIssue pullRequest = event.pullRequest();
        if (pullRequest == null) {
            throw new ExtractException(event);
        }
        pullRequest.setIsPullRequest(true);
        return pullRequest;
    }
}
