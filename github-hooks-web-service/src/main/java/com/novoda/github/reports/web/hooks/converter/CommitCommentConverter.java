package com.novoda.github.reports.web.hooks.converter;

import com.novoda.github.reports.data.model.Event;
import com.novoda.github.reports.data.model.EventType;
import com.novoda.github.reports.service.issue.GithubComment;
import com.novoda.github.reports.service.persistence.converter.ConverterException;
import com.novoda.github.reports.service.repository.GithubRepository;
import com.novoda.github.reports.web.hooks.model.CommitComment;
import com.novoda.github.reports.web.hooks.model.GithubAction;

public class CommitCommentConverter implements EventConverter<CommitComment> {

    @Override
    public Event convertFrom(CommitComment commitComment) throws ConverterException {
        GithubComment githubComment = commitComment.getComment();
        GithubRepository githubRepository = commitComment.getRepository();
        EventType eventType = getEventType(commitComment);
        return Event.create(
                githubComment.getId(),
                githubRepository.getId(),
                githubComment.getUserId(),
                githubRepository.getOwnerId(),
                eventType,
                githubComment.getUpdatedAt()
        );
    }

    private EventType getEventType(CommitComment commitComment) throws ConverterException {
        try {
            return convertAction(commitComment.getAction());
        } catch (UnsupportedActionException e) {
            throw new ConverterException(e);
        }
    }

    @Override
    public EventType convertAction(GithubAction action) throws UnsupportedActionException {
        switch (action) {
            case CREATED:
                // we currently don't have a specific event type for a commit comment
                return EventType.PULL_REQUEST_COMMENT;
            case EDITED:
            case DELETED:
                // no db support
            default:
                throw new UnsupportedActionException(action);
        }
    }
}
