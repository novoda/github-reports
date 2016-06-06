package com.novoda.github.reports.batch.issue;

import com.novoda.github.reports.batch.User;
import com.novoda.github.reports.batch.repository.Repository;

import java.util.Date;

public abstract class RepositoryIssueEvent {

    private final Repository repository;
    private final Issue issue;

    protected RepositoryIssueEvent(RepositoryIssue repositoryIssue) {
        this.repository = repositoryIssue.getRepository();
        this.issue = repositoryIssue.getIssue();
    }

    public Repository getRepository() {
        return repository;
    }

    public Issue getIssue() {
        return issue;
    }

    public boolean isIssue() {
        return !issue.isPullRequest();
    }

    public abstract Long getEventId();

    public abstract User getUser();

    public abstract Date getDate();

    public abstract Event.Type getEventType();
}
