package com.novoda.github.reports.web.hooks.model;

import com.novoda.github.reports.service.issue.GithubComment;

@Deprecated
public class Deprecated_ReviewComment {

    private GithubWebhookPullRequest webhookPullRequest;
    private GithubComment comment;

    public Deprecated_ReviewComment(GithubWebhookPullRequest webhookPullRequest, GithubComment comment) {
        this.webhookPullRequest = webhookPullRequest;
        this.comment = comment;
    }

    public GithubWebhookPullRequest getWebhookPullRequest() {
        return webhookPullRequest;
    }

    public GithubComment getComment() {
        return comment;
    }
}
