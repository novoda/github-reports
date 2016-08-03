package com.novoda.github.reports.web.hooks.converter;

import com.novoda.github.reports.data.model.Event;
import com.novoda.github.reports.data.model.EventType;
import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.service.persistence.converter.ConverterException;
import com.novoda.github.reports.service.repository.GithubRepository;
import com.novoda.github.reports.web.hooks.model.GithubAction;
import com.novoda.github.reports.web.hooks.model.Issue;

public class IssueConverter implements EventConverter<Issue> {

    @Override
    public Event convertFrom(Issue issue) throws ConverterException {
        GithubIssue githubIssue = issue.getIssue();
        GithubRepository githubRepository = issue.getRepository();
        EventType eventType = getEventType(issue);
        return Event.create(
                githubIssue.getId(),
                githubRepository.getId(),
                githubIssue.getUserId(),
                githubIssue.getUserId(),
                eventType,
                githubIssue.getUpdatedAt()
        );
    }

    private EventType getEventType(Issue issue) throws ConverterException {
        try {
            return convertAction(issue.getAction());
        } catch (UnsupportedActionException e) {
            throw new ConverterException(e);
        }
    }

    @Override
    public EventType convertAction(GithubAction action) throws UnsupportedActionException {
        switch (action) {
            case UNLABELED:
                return EventType.ISSUE_LABEL_REMOVE;
            case OPENED:
                return EventType.ISSUE_OPEN;
            case LABELED:
                return EventType.ISSUE_LABEL_ADD;
            case CLOSED:
                return EventType.ISSUE_CLOSE;
            case EDITED:
            case REOPENED:
            case ASSIGNED:
            case UNASSIGNED:
                // no db support
        }

        throw new UnsupportedActionException(action);
    }
}
