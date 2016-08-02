package com.novoda.github.reports.web.hooks.model;

import com.novoda.github.reports.service.issue.GithubComment;

public class ReviewComment {

    private GithubWebhookPullRequest webhookPullRequest;
    private GithubComment comment;

    public ReviewComment(GithubWebhookPullRequest webhookPullRequest, GithubComment comment) {
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
