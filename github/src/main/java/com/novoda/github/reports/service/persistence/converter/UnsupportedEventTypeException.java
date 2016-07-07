package com.novoda.github.reports.service.persistence.converter;

import com.novoda.github.reports.service.issue.GithubEvent;

class UnsupportedEventTypeException extends Throwable {
    UnsupportedEventTypeException(GithubEvent.Type type) {
        super("Event type " + type.toString() + " is not supported and should have been filtered out.");
    }
}
