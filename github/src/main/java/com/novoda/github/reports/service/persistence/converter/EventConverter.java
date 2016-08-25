package com.novoda.github.reports.service.persistence.converter;

import com.novoda.github.reports.data.model.Event;
import com.novoda.github.reports.data.model.EventType;
import com.novoda.github.reports.service.issue.RepositoryIssueEvent;

import static com.novoda.github.reports.service.issue.RepositoryIssueEvent.Type.*;

public class EventConverter implements Converter<RepositoryIssueEvent, Event> {

    public static EventConverter newInstance() {
        return new EventConverter();
    }

    @Override
    public Event convertFrom(RepositoryIssueEvent repositoryIssueEvent) throws ConverterException {
        EventType eventType;

        try {
            eventType = convertEventType(repositoryIssueEvent.getEventType(), repositoryIssueEvent.isIssue());
        } catch (UnsupportedEventTypeException e) {
            throw new ConverterException(e);
        }

        return Event.create(
                repositoryIssueEvent.getEventId(),
                repositoryIssueEvent.getRepositoryId(),
                repositoryIssueEvent.getAuthorUserId(),
                repositoryIssueEvent.getOwnerUserId(),
                eventType,
                repositoryIssueEvent.getDate()
        );
    }

    private EventType convertEventType(RepositoryIssueEvent.Type type, boolean isIssue) throws UnsupportedEventTypeException {
        if (type == HEAD_REF_DELETED) {
            return EventType.BRANCH_DELETE;
        }

        if (type == COMMENTED) {
            if (isIssue) {
                return EventType.ISSUE_COMMENT;
            }
            return EventType.PULL_REQUEST_COMMENT;
        }

        if (type == CLOSED) {
            if (isIssue) {
                return EventType.ISSUE_CLOSE;
            }
            return EventType.PULL_REQUEST_CLOSE;
        }

        if (type == LABELED) {
            if (isIssue) {
                return EventType.ISSUE_LABEL_ADD;
            }
            return EventType.PULL_REQUEST_LABEL_ADD;
        }

        if (type == UNLABELED) {
            if (isIssue) {
                return EventType.ISSUE_LABEL_REMOVE;
            }
            return EventType.PULL_REQUEST_LABEL_REMOVE;
        }

        if (type == MERGED) {
            return EventType.PULL_REQUEST_MERGE;
        }

        if (type == REACTED) {
            if (isIssue) {
                return EventType.ISSUE_REACTION;
            }
            return EventType.PULL_REQUEST_REACTION;
        }

        throw new UnsupportedEventTypeException(type);
    }

}
