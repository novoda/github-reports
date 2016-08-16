package com.novoda.github.reports.web.hooks.model;

import com.novoda.github.reports.service.issue.GithubComment;
import com.novoda.github.reports.service.repository.GithubRepository;

public class ReviewComment extends Event {

    private GithubComment comment;
    private GithubRepository repository;
    private GithubWebhookPullRequest webhookPullRequest;

    public ReviewComment(GithubComment comment, GithubRepository repository, GithubWebhookPullRequest pullRequest, GithubAction action) {
        this.comment = comment;
        this.repository = repository;
        this.webhookPullRequest = pullRequest;
        this.action = action;
    }

    public GithubComment getComment() {
        return comment;
    }

    public GithubRepository getRepository() {
        return repository;
    }

    public GithubWebhookPullRequest getWebhookPullRequest() {
        return webhookPullRequest;
    }
}
