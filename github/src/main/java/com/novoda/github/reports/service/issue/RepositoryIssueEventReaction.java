package com.novoda.github.reports.service.issue;

import com.novoda.github.reports.service.GithubUser;

import java.util.Date;

public class RepositoryIssueEventReaction extends RepositoryIssueEvent {

    private final GithubReaction reaction;

    public RepositoryIssueEventReaction(RepositoryIssue repositoryIssue, GithubReaction reaction) {
        super(repositoryIssue);
        this.reaction = reaction;
    }

    @Override
    public Long getEventId() {
        return reaction.getId();
    }

    @Override
    public GithubUser getUser() {
        return reaction.getUser();
    }

    @Override
    public Date getDate() {
        return reaction.getCreatedAt();
    }

    @Override
    public RepositoryIssueEvent.Type getEventType() {
        return RepositoryIssueEvent.Type.REACTED;
    }

    @Override
    public String toString() {
        return reaction.toString() +
                " on issue " + getIssue().toString() +
                " for repository " + getRepository().getName();
    }
}
