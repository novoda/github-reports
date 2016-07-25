package com.novoda.github.reports.web.hooks.handler;

import com.novoda.github.reports.service.persistence.ConnectionManagerContainer;
import com.novoda.github.reports.web.hooks.lambda.GithubWebhookEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class HandlerRouter {

    private final List<EventHandler> handlers;

    public static HandlerRouter newInstance() {
        return new HandlerRouter(Collections.unmodifiableList(Arrays.asList(
                // TODO more handlers
                PullRequestHandler.newInstance(ConnectionManagerContainer.getConnectionManager())
        )));
    }

    HandlerRouter(List<EventHandler> handlers) {
        this.handlers = handlers;
    }

    public void route(GithubWebhookEvent event) throws UnhandledEventException {
        boolean eventWasNotHandled = handlers
                .stream()
                .noneMatch(handleEventIfPossible(event));
        if (eventWasNotHandled) {
            throw new UnhandledEventException(event);
        }
    }

    private Predicate<EventHandler> handleEventIfPossible(GithubWebhookEvent event) {
        return eventHandler -> {
            try {
                return eventHandler.handle(event);
            } catch (UnhandledEventException e) {
                return false;
            }
        };
    }

}
