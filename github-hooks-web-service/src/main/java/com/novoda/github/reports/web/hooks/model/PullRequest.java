package com.novoda.github.reports.web.hooks.model;

import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.service.repository.GithubRepository;

public class PullRequest {

    private GithubIssue issue;
    private GithubRepository repository;

    public PullRequest(GithubIssue issue, GithubRepository repository) {
        this.issue = issue;
        this.repository = repository;
    }

    public GithubIssue getIssue() {
        return issue;
    }

    public GithubRepository getRepository() {
        return repository;
    }
}
