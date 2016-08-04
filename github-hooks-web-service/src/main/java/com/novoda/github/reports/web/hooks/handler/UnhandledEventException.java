package com.novoda.github.reports.web.hooks.handler;

public class UnhandledEventException extends Exception {

    UnhandledEventException(String message) {
        super(message);
    }

    public UnhandledEventException(Throwable cause) {
        super(cause);
    }
}
