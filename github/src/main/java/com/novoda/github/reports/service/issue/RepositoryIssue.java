package com.novoda.github.reports.service.issue;

import com.novoda.github.reports.service.repository.GithubRepository;

public class RepositoryIssue {

    private final GithubRepository repository;
    private final GithubIssue issue;

    public RepositoryIssue(GithubRepository repository, GithubIssue issue) {
        this.repository = repository;
        this.issue = issue;
    }

    public GithubRepository getRepository() {
        return repository;
    }

    public GithubIssue getIssue() {
        return issue;
    }

    public String getOwnerUsername() {
        return repository.getOwnerUsername();
    }

    public String getRepositoryName() {
        return repository.getName();
    }

    public boolean isPullRequest() {
        return issue.isPullRequest();
    }

    public int getIssueNumber() {
        return issue.getNumber();
    }

    public Long getUserId() {
        return issue.getUserId();
    }
}
