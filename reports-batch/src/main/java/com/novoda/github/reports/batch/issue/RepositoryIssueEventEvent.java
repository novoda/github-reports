package com.novoda.github.reports.batch.issue;

import com.novoda.github.reports.batch.User;

import java.util.Date;

public class RepositoryIssueEventEvent extends RepositoryIssueEvent {

    private final Event event;

    public static RepositoryIssueEvent newInstance(RepositoryIssue repositoryIssue, Event event) {
        return new RepositoryIssueEventEvent(repositoryIssue, event);
    }

    private RepositoryIssueEventEvent(RepositoryIssue repositoryIssue, Event event) {
        super(repositoryIssue);
        this.event = event;
    }

    @Override
    public Long getEventId() {
        return event.getId();
    }

    @Override
    public User getUser() {
        return event.getActor();
    }

    @Override
    public Date getDate() {
        return event.getCreatedAt();
    }

    @Override
    public Event.Type getEventType() {
        return event.getType();
    }

    @Override
    public String toString() {
        return event.toString();
    }
}
