package com.novoda.github.reports.batch.persistence.converter;

import com.novoda.github.reports.batch.issue.RepositoryIssueEvent;
import com.novoda.github.reports.data.model.Event;
import com.novoda.github.reports.data.model.EventType;
import static com.novoda.github.reports.batch.issue.Event.Type.*;
import static com.novoda.github.reports.data.model.EventType.PULL_REQUEST_MERGE;

public class EventConverter implements Converter<RepositoryIssueEvent, Event> {

    public static EventConverter newInstance() {
        return new EventConverter();
    }

    @Override
    public Event convertFrom(RepositoryIssueEvent repositoryIssueEvent) throws ConverterException {
        EventType eventType;
        try {
            eventType = convertEventType(repositoryIssueEvent.getEventType(), repositoryIssueEvent.isComment());
        } catch (UnsupportedEventTypeException e) {
            throw new ConverterException(e);
        }

        return Event.create(
                repositoryIssueEvent.getEventId(),
                repositoryIssueEvent.getRepository().getId(),
                repositoryIssueEvent.getUser().getId(),
                repositoryIssueEvent.getIssue().getUser().getId(),
                eventType,
                repositoryIssueEvent.getDate()
        );
    }

    private EventType convertEventType(com.novoda.github.reports.batch.issue.Event.Type type, boolean isComment) throws UnsupportedEventTypeException {
        if (type == HEAD_REF_DELETED) {
            return EventType.BRANCH_DELETE;
        }

        if (type == COMMENTED) {
            if (isComment) {
                return EventType.ISSUE_COMMENT;
            }
            return EventType.PULL_REQUEST_COMMENT;
        }

        if (type == CLOSED) {
            if (isComment) {
                return EventType.ISSUE_CLOSE;
            }
            return EventType.PULL_REQUEST_CLOSE;
        }

        if (type == LABELED) {
            if (isComment) {
                return EventType.ISSUE_LABEL_ADD;
            }
            return EventType.PULL_REQUEST_LABEL_ADD;
        }

        if (type == UNLABELED) {
            if (isComment) {
                return EventType.ISSUE_LABEL_REMOVE;
            }
            return EventType.PULL_REQUEST_LABEL_REMOVE;
        }

        if (type == MERGED) {
            return PULL_REQUEST_MERGE;
        }

        throw new UnsupportedEventTypeException(type);
    }

}
