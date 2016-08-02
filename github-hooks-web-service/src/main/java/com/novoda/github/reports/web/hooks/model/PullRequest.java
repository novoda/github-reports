package com.novoda.github.reports.web.hooks.model;

import com.novoda.github.reports.service.repository.GithubRepository;

public class PullRequest extends Event {

    private GithubWebhookPullRequest webhookPullRequest;
    private GithubRepository repository;

    public PullRequest(GithubWebhookPullRequest webhookPullRequest, GithubRepository repository, GithubAction action) {
        this.webhookPullRequest = webhookPullRequest;
        this.repository = repository;
        this.action = action;
    }

    public GithubWebhookPullRequest getWebhookPullRequest() {
        return webhookPullRequest;
    }

    public GithubRepository getRepository() {
        return repository;
    }
}
