package com.novoda.github.reports.service.issue;

import com.novoda.github.reports.service.GithubUser;

import java.util.Date;

public class RepositoryIssueEventEvent extends RepositoryIssueEvent {

    private final GithubEvent event;

    public static RepositoryIssueEventEvent newInstance(RepositoryIssue repositoryIssue, GithubEvent event) {
        return new RepositoryIssueEventEvent(repositoryIssue, event);
    }

    private RepositoryIssueEventEvent(RepositoryIssue repositoryIssue, GithubEvent event) {
        super(repositoryIssue);
        this.event = event;
    }

    @Override
    public Long getEventId() {
        return event.getId();
    }

    @Override
    public GithubUser getUser() {
        return event.getActor();
    }

    @Override
    public Date getDate() {
        return event.getCreatedAt();
    }

    @Override
    public RepositoryIssueEvent.Type getEventType() {
        String originalEventValue = event.getType().toString();
        return RepositoryIssueEvent.Type.valueOf(originalEventValue);
    }

    @Override
    public String toString() {
        return event.toString() +
                " on issue " + getIssue().toString() +
                " for repository " + getRepository().getName();
    }
}
