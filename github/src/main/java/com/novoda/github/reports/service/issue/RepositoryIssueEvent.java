package com.novoda.github.reports.service.issue;

import com.novoda.github.reports.service.GithubUser;
import com.novoda.github.reports.service.repository.GithubRepository;

import java.util.Date;

public abstract class RepositoryIssueEvent {

    private final GithubRepository repository;
    private final GithubIssue issue;

    protected RepositoryIssueEvent(RepositoryIssue repositoryIssue) {
        this.repository = repositoryIssue.getRepository();
        this.issue = repositoryIssue.getIssue();
    }

    public GithubRepository getRepository() {
        return repository;
    }

    public Long getRepositoryId() {
        return repository.getId();
    }

    public Long getAuthorUserId() {
        return getUser().getId();
    }

    public Long getOwnerUserId() {
        return getIssue().getUserId();
    }

    public GithubIssue getIssue() {
        return issue;
    }

    public boolean isIssue() {
        return !issue.isPullRequest();
    }

    public abstract Long getEventId();

    public abstract GithubUser getUser();

    public abstract Date getDate();

    public abstract GithubEvent.Type getEventType();
}
