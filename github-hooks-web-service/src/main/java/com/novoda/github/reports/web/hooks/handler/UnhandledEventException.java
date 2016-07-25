package com.novoda.github.reports.web.hooks.handler;

import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;

public class UnhandledEventException extends Exception {

    UnhandledEventException(String message) {
        super(message);
    }

    UnhandledEventException(GithubWebhookEvent event) {
        super("Unhandled event: " + event.toString());
    }
}
