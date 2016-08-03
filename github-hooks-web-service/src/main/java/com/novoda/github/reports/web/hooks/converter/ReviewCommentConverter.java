package com.novoda.github.reports.web.hooks.converter;

import com.novoda.github.reports.data.model.Event;
import com.novoda.github.reports.data.model.EventType;
import com.novoda.github.reports.service.issue.GithubComment;
import com.novoda.github.reports.service.persistence.converter.ConverterException;
import com.novoda.github.reports.service.repository.GithubRepository;
import com.novoda.github.reports.web.hooks.model.ReviewComment;
import com.novoda.github.reports.web.hooks.model.GithubAction;

public class ReviewCommentConverter implements EventConverter<ReviewComment> {

    @Override
    public Event convertFrom(ReviewComment reviewComment) throws ConverterException {
        GithubComment githubComment = reviewComment.getComment();
        GithubRepository githubRepository = reviewComment.getRepository();
        EventType eventType = getEventType(reviewComment);
        return Event.create(
                githubComment.getId(),
                githubRepository.getId(),
                githubComment.getUserId(),
                githubRepository.getOwnerId(),
                eventType,
                githubComment.getUpdatedAt()
        );
    }

    private EventType getEventType(ReviewComment reviewComment) throws ConverterException {
        try {
            return convertAction(reviewComment.getAction());
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
        }

        throw new UnsupportedActionException(action);
    }
}
