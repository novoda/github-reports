package com.novoda.github.reports.web.hooks.convert;

import com.novoda.github.reports.data.model.Event;
import com.novoda.github.reports.data.model.EventType;
import com.novoda.github.reports.service.persistence.converter.ConverterException;
import com.novoda.github.reports.service.repository.GithubRepository;
import com.novoda.github.reports.web.hooks.model.GithubAction;
import com.novoda.github.reports.web.hooks.model.GithubWebhookPullRequest;
import com.novoda.github.reports.web.hooks.model.PullRequest;

public class PullRequestToDbEventConverter implements EventConverter<PullRequest> {

    @Override
    public Event convertFrom(PullRequest pullRequest) throws ConverterException {

        GithubWebhookPullRequest webhookPullRequest = pullRequest.getWebhookPullRequest();
        GithubRepository repository = pullRequest.getRepository();

        EventType eventType = convertPullRequestAction(pullRequest);

        return Event.create(
                webhookPullRequest.getId(),
                repository.getId(),
                webhookPullRequest.getUserId(),
                webhookPullRequest.getUserId(),
                eventType,
                webhookPullRequest.getUpdatedAt()
        );
    }

    private EventType convertPullRequestAction(PullRequest pullRequest) throws ConverterException {
        EventType eventType = convertActionOrThrow(pullRequest);
        if (eventType == EventType.PULL_REQUEST_CLOSE) {
            return convertClosedAction(pullRequest);
        }
        return eventType;
    }

    private EventType convertActionOrThrow(PullRequest pullRequest) throws ConverterException {
        try {
            return convertAction(pullRequest.getAction());
        } catch (UnsupportedActionException e) {
            throw new ConverterException(e);
        }
    }

    @Override
    public EventType convertAction(GithubAction action) throws UnsupportedActionException {
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

        throw new UnsupportedActionException(action);
    }

    private EventType convertClosedAction(PullRequest pullRequest) {
        if (isMerged(pullRequest)) {
            return EventType.PULL_REQUEST_MERGE;
        }
        return EventType.PULL_REQUEST_CLOSE;
    }

    private boolean isMerged(PullRequest pullRequest) {
        return pullRequest.getWebhookPullRequest().getPullRequest().isMerged();
    }
}
