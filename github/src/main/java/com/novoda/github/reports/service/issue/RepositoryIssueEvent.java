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

    public abstract Type getEventType();

    public enum Type {

        COMMENTED("commented"),
        CLOSED("closed"),
        HEAD_REF_DELETED("head_ref_deleted"),
        LABELED("labeled"),
        MERGED("merged"),
        REACTED("reacted"),
        UNLABELED("unlabeled");

        private final String event;

        Type(String event) {
            this.event = event;
        }

        @Override
        public String toString() {
            return event;
        }

        public static Type fromEvent(String event) {
            for (Type type : Type.values()) {
                if (type.toString().equals(event)) {
                    return type;
                }
            }
            throw new IllegalArgumentException(String.format("No type exists for event \"%s\".", event));
        }

    }
}
