package com.novoda.github.reports.batch.persistence.converter;

import com.novoda.github.reports.batch.issue.Event;

class UnsupportedEventTypeException extends Throwable {
    UnsupportedEventTypeException(Event.Type type) {
        super("Event type " + type.toString() + " is not supported and should have been filtered out.");
    }
}
