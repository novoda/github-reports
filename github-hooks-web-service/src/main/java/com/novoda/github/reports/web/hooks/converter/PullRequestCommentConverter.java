package com.novoda.github.reports.web.hooks.converter;

import com.novoda.github.reports.data.model.Event;
import com.novoda.github.reports.data.model.EventType;
import com.novoda.github.reports.service.issue.GithubComment;
import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.service.persistence.converter.ConverterException;
import com.novoda.github.reports.service.repository.GithubRepository;
import com.novoda.github.reports.web.hooks.model.GithubAction;
import com.novoda.github.reports.web.hooks.model.PullRequestComment;

public class PullRequestCommentConverter implements EventConverter<PullRequestComment> {

    @Override
    public Event convertFrom(PullRequestComment pullRequestComment) throws ConverterException {
        GithubComment githubComment = pullRequestComment.getComment();
        GithubRepository githubRepository = pullRequestComment.getRepository();
        GithubIssue githubIssue = pullRequestComment.getIssue();
        EventType eventType = getEventType(pullRequestComment);
        return Event.create(
                githubComment.getId(),
                githubRepository.getId(),
                githubComment.getUserId(),
                githubIssue.getUserId(),
                eventType,
                githubComment.getUpdatedAt()
        );
    }

    private EventType getEventType(PullRequestComment pullRequestComment) throws ConverterException {
        try {
            return convertAction(pullRequestComment.getAction());
        } catch (UnsupportedActionException e) {
            throw new ConverterException(e);
        }
    }

    @Override
    public EventType convertAction(GithubAction action) throws UnsupportedActionException {
        switch (action) {
            case CREATED:
                return EventType.PULL_REQUEST_COMMENT;
            case EDITED:
            case DELETED:
                // no db support
            default:
                throw new UnsupportedActionException(action);
        }
    }

}
