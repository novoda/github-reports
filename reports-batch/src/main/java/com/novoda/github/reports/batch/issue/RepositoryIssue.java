package com.novoda.github.reports.batch.issue;

import com.novoda.github.reports.batch.repository.GithubRepository;

public class RepositoryIssue {

    private final GithubRepository repository;

    private final GithubIssue issue;

    public static RepositoryIssue newInstance(GithubRepository repository, GithubIssue issue) {
        return new RepositoryIssue(repository, issue);
    }

    private RepositoryIssue(GithubRepository repository, GithubIssue issue) {
        this.repository = repository;
        this.issue = issue;
    }

    public GithubRepository getRepository() {
        return repository;
    }

    public GithubIssue getIssue() {
        return issue;
    }
}
