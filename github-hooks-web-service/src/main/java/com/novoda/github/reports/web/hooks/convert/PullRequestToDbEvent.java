package com.novoda.github.reports.web.hooks.convert;

import com.novoda.github.reports.data.model.Event;
import com.novoda.github.reports.data.model.EventType;
import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.service.persistence.converter.ConverterException;
import com.novoda.github.reports.service.repository.GithubRepository;
import com.novoda.github.reports.web.hooks.model.GithubAction;
import com.novoda.github.reports.web.hooks.model.PullRequest;

public class PullRequestToDbEvent implements EventConverter<PullRequest, Event> {

    @Override
    public Event convertFrom(PullRequest pullRequest) throws ConverterException {

        GithubIssue issue = pullRequest.getIssue();
        GithubRepository repository = pullRequest.getRepository();

        EventType eventType = convertPullRequestAction(pullRequest);

        return Event.create(
                issue.getId(),
                repository.getId(),
                issue.getUserId(),
                issue.getUserId(),
                eventType,
                issue.getUpdatedAt()
        );
    }

    private EventType convertPullRequestAction(PullRequest pullRequest) {
        EventType eventType = convertAction(pullRequest.getAction());
        if (eventType == EventType.PULL_REQUEST_CLOSE) {
            // TODO extract
            return pullRequest.getIssue().getPullRequest().isMerged() ?
                    EventType.PULL_REQUEST_MERGE
                    : EventType.PULL_REQUEST_CLOSE;
        }
        return eventType;
    }

    @Override
    public EventType convertAction(GithubAction action) {
        switch (action) {
            case LABELED:
                return EventType.PULL_REQUEST_LABEL_ADD;
            case UNLABELED:
                return EventType.PULL_REQUEST_LABEL_REMOVE;
            case OPENED:
                return EventType.PULL_REQUEST_OPEN;
            case CLOSED:
                return EventType.PULL_REQUEST_CLOSE;
            case EDITED:
                // no db support
                break;
            case REOPENED:
                // no db support
                break;
            case SYNCHRONIZE:
                // no db support
                break;
        }

        throw new IllegalStateException("Unable to convert action: " + action.toString());
    }
}
