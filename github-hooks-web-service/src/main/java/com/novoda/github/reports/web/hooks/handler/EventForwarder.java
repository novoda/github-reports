package com.novoda.github.reports.web.hooks.handler;

import com.novoda.github.reports.service.persistence.ConnectionManagerContainer;
import com.novoda.github.reports.web.hooks.classification.ClassificationException;
import com.novoda.github.reports.web.hooks.classification.EventType;
import com.novoda.github.reports.web.hooks.classification.WebhookEventClassifier;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EventForwarder {

    private static final Map<EventType, EventHandler> HANDLERS = new HashMap<>(5);
    static {
        HANDLERS.put(EventType.PULL_REQUEST, PullRequestHandler.newInstance(ConnectionManagerContainer.getConnectionManager()));
        HANDLERS.put(EventType.ISSUE, IssueHandler.newInstance(ConnectionManagerContainer.getConnectionManager()));
        HANDLERS.put(EventType.COMMIT_COMMENT, CommitCommentHandler.newInstance());
        HANDLERS.put(EventType.ISSUE_COMMENT, IssueCommentHandler.newInstance());
        HANDLERS.put(EventType.REVIEW_COMMENT, ReviewCommentHandler.newInstance());
    }

    private WebhookEventClassifier eventClassifier;
    private final Map<EventType, EventHandler> handlers;

    public static EventForwarder newInstance() {
        WebhookEventClassifier eventClassifier = new WebhookEventClassifier();
        return new EventForwarder(eventClassifier, Collections.unmodifiableMap(HANDLERS));
    }

    EventForwarder(WebhookEventClassifier eventClassifier, Map<EventType, EventHandler> handlers) {
        this.eventClassifier = eventClassifier;
        this.handlers = handlers;
    }

    public void forward(GithubWebhookEvent event) throws UnhandledEventException {
        EventType eventType = classify(event);
        EventHandler eventHandler = handlers.get(eventType);

        if (eventHandler == null) {
            throw new UnhandledEventException("No registered handler for event: " + event.toString());
        }

        handlers.get(eventType).handle(event);
    }

    private EventType classify(GithubWebhookEvent event) throws UnhandledEventException {
        EventType eventType;
        try {
            eventType = eventClassifier.classify(event);
        } catch (ClassificationException e) {
            throw new UnhandledEventException(e.getMessage());
        }
        return eventType;
    }
}
