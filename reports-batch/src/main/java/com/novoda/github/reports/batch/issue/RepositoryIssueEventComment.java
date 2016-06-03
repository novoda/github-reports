package com.novoda.github.reports.batch.issue;

import com.novoda.github.reports.batch.User;

import java.util.Date;

public class RepositoryIssueEventComment extends RepositoryIssueEvent {

    private final Comment comment;

    public static RepositoryIssueEvent newInstance(RepositoryIssue repositoryIssue, Comment comment) {
        return new RepositoryIssueEventComment(repositoryIssue, comment);
    }

    private RepositoryIssueEventComment(RepositoryIssue repositoryIssue, Comment comment) {
        super(repositoryIssue);
        this.comment = comment;
    }

    @Override
    public Long getEventId() {
        return comment.getId();
    }

    @Override
    public User getUser() {
        return comment.getUser();
    }

    @Override
    public Date getDate() {
        return comment.getCreatedAt();
    }

    @Override
    public Event.Type getEventType() {
        return Event.Type.COMMENTED;
    }

    @Override
    public boolean isComment() {
        return true;
    }

    @Override
    public String toString() {
        return comment.toString();
    }
}
