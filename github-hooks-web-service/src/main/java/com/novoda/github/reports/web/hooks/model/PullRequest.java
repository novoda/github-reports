package com.novoda.github.reports.web.hooks.model;

import com.novoda.github.reports.service.repository.GithubRepository;

public class PullRequest extends Event {

    private GithubWebhookPullRequest issue;
    private GithubRepository repository;

    public PullRequest(GithubWebhookPullRequest issue, GithubRepository repository, GithubAction action) {
        this.issue = issue;
        this.repository = repository;
        this.action = action;
    }

    public GithubWebhookPullRequest getIssue() {
        return issue;
    }

    public GithubRepository getRepository() {
        return repository;
    }
}
