package com.novoda.github.reports.batch.issue;

import com.novoda.github.reports.batch.repository.Repository;

public class RepositoryIssue {

    private final Repository repository;

    private final Issue issue;

    public static RepositoryIssue newInstance(Repository repository, Issue issue) {
        return new RepositoryIssue(repository, issue);
    }

    private RepositoryIssue(Repository repository, Issue issue) {
        this.repository = repository;
        this.issue = issue;
    }

    public Repository getRepository() {
        return repository;
    }

    public Issue getIssue() {
        return issue;
    }
}
