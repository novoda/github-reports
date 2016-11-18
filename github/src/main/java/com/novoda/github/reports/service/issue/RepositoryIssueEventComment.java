package com.novoda.github.reports.service.issue;

import com.novoda.github.reports.service.GithubUser;

import java.util.Date;

public class RepositoryIssueEventComment extends RepositoryIssueEvent {

    private final GithubComment comment;

    public RepositoryIssueEventComment(RepositoryIssue repositoryIssue, GithubComment comment) {
        super(repositoryIssue);
        this.comment = comment;
    }

    @Override
    public Long getEventId() {
        return comment.getId();
    }

    @Override
    public GithubUser getUser() {
        return comment.getUser();
    }

    @Override
    public Date getDate() {
        return comment.getCreatedAt();
    }

    @Override
    public RepositoryIssueEvent.Type getEventType() {
        return RepositoryIssueEvent.Type.COMMENTED;
    }

    @Override
    public String toString() {
        return comment.toString() +
                " on issue " + getIssue().toString() +
                " for repository " + getRepository().getName();
    }
}
