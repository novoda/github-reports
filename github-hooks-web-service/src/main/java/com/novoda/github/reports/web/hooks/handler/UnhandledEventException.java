package com.novoda.github.reports.web.hooks.handler;

import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;

public class UnhandledEventException extends Exception {

    UnhandledEventException(String message) {
        super(message);
    }

    public UnhandledEventException(Throwable cause) {
        super(cause);
    }

    UnhandledEventException(GithubWebhookEvent event, String message) {
        super("Unhandled event: " + event.toString());
    }
}
