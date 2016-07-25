package com.novoda.github.reports.web.hooks.handler;

import com.novoda.github.reports.web.hooks.classification.EventType;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;

interface EventHandler {

    void handle(GithubWebhookEvent event) throws UnhandledEventException;

    EventType handledEventType();

}
