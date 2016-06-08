package com.novoda.github.reports.batch.persistence.converter;

import com.novoda.github.reports.batch.issue.GithubEvent;

class UnsupportedEventTypeException extends Throwable {
    UnsupportedEventTypeException(GithubEvent.Type type) {
        super("Event type " + type.toString() + " is not supported and should have been filtered out.");
    }
}
