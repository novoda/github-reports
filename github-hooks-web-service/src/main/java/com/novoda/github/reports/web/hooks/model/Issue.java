package com.novoda.github.reports.web.hooks.model;

import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.service.repository.GithubRepository;

public class Issue extends Event {

    private GithubIssue issue;
    private GithubRepository repository;

    public Issue(GithubIssue issue, GithubRepository repository, GithubAction action) {
        this.issue = issue;
        this.repository = repository;
        this.action = action;
    }

    public GithubIssue getIssue() {
        return issue;
    }

    public GithubRepository getRepository() {
        return repository;
    }
}
