package com.novoda.github.reports.web.hooks.handler;

import com.novoda.github.reports.service.persistence.ConnectionManagerContainer;
import com.novoda.github.reports.web.hooks.lambda.GithubWebhookEvent;
import com.novoda.github.reports.web.hooks.parse.EventHandler;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HandlerRouter {

    private static final List<EventHandler> handlers = Collections.unmodifiableList(Arrays.asList(
            // TODO more handlers
            PullRequestHandler.newInstance(ConnectionManagerContainer.getConnectionManager())
    ));

    public void route(GithubWebhookEvent event) throws UnhandledEventException {
        boolean eventWasNotHandled = handlers
                .stream()
                .noneMatch(eventHandler -> eventHandler.handle(event));
        if (eventWasNotHandled) {
            throw new UnhandledEventException(event);
        }
    }

    public static class UnhandledEventException extends Exception {
        public UnhandledEventException(GithubWebhookEvent event) {
            super("Unhandled event: " + event.toString());
        }
    }

}
