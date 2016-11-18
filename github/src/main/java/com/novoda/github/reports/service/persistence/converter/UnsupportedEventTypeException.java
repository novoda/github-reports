package com.novoda.github.reports.service.persistence.converter;

import com.novoda.github.reports.service.issue.RepositoryIssueEvent;

class UnsupportedEventTypeException extends Exception {
    UnsupportedEventTypeException(RepositoryIssueEvent.Type type) {
        super("Event type " + type.toString() + " is not supported and should have been filtered out.");
    }
}
