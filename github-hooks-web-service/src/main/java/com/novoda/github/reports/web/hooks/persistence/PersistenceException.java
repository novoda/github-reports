package com.novoda.github.reports.web.hooks.persistence;

import com.novoda.github.reports.web.hooks.model.Event;

public class PersistenceException extends Exception {

    PersistenceException(String message) {
        super(message);
    }

    PersistenceException(Event event) {
        super("Unable to persist event: " + event.toString());
    }

    public PersistenceException(Throwable cause) {
        super(cause);
    }

    public PersistenceException(Event event, Exception cause) {
        super("Unable to persist event: " + event.toString(), cause);
    }
}
