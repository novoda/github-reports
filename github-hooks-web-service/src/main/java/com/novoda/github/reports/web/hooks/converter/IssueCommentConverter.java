package com.novoda.github.reports.web.hooks.converter;

import com.novoda.github.reports.data.model.Event;
import com.novoda.github.reports.data.model.EventType;
import com.novoda.github.reports.service.issue.GithubComment;
import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.service.persistence.converter.ConverterException;
import com.novoda.github.reports.service.repository.GithubRepository;
import com.novoda.github.reports.web.hooks.model.GithubAction;
import com.novoda.github.reports.web.hooks.model.IssueComment;

public class IssueCommentConverter implements EventConverter<IssueComment> {

    @Override
    public Event convertFrom(IssueComment issueComment) throws ConverterException {
        GithubComment githubComment = issueComment.getComment();
        GithubRepository githubRepository = issueComment.getRepository();
        GithubIssue githubIssue = issueComment.getIssue();
        EventType eventType = getEventType(issueComment);
        return Event.create(
                githubComment.getId(),
                githubRepository.getId(),
                githubComment.getUserId(),
                githubIssue.getUserId(), // TODO check if we want the issues' user id or the repo's owner id
                eventType,
                githubComment.getUpdatedAt()
        );
    }

    private EventType getEventType(IssueComment issueComment) throws ConverterException {
        try {
            return convertAction(issueComment.getAction());
        } catch (UnsupportedActionException e) {
            throw new ConverterException(e);
        }
    }

    @Override
    public EventType convertAction(GithubAction action) throws UnsupportedActionException {
        switch (action) {
            case CREATED:
                return EventType.ISSUE_COMMENT;
            case EDITED:
            case DELETED:
                // no db support
        }

        throw new UnsupportedActionException(action);
    }
}
