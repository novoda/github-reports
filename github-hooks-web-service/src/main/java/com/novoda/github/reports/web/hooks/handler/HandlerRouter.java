package com.novoda.github.reports.web.hooks.handler;

import com.novoda.github.reports.web.hooks.EventType;
import com.novoda.github.reports.web.hooks.extract.WebhookPayloadExtractor;
import com.novoda.github.reports.web.hooks.lambda.GithubWebhookEvent;
import com.novoda.github.reports.web.hooks.parse.EventHandler;
import com.novoda.github.reports.web.hooks.parse.WebhookEventClassifier;

import java.util.HashMap;
import java.util.Map;

public class HandlerRouter {

    private static final Map<EventType, EventHandler> HANDLERS = new HashMap<>(5); // TODO unmodifiable?
    static {
        HANDLERS.put(EventType.COMMIT_COMMENT, new PullRequestHandler());   // TODO proper handler
        HANDLERS.put(EventType.ISSUE, new PullRequestHandler());            // TODO proper handler
        HANDLERS.put(EventType.PULL_REQUEST, new PullRequestHandler());
        HANDLERS.put(EventType.ISSUE_COMMENT, new PullRequestHandler());    // TODO proper handler
        HANDLERS.put(EventType.REVIEW_COMMENT, new PullRequestHandler());   // TODO proper handler
    }

    private WebhookEventClassifier eventClassifier;
    private WebhookPayloadExtractor eventExtractor;

    public static HandlerRouter newInstance() {
        WebhookEventClassifier eventClassifier = new WebhookEventClassifier();
        WebhookPayloadExtractor payloadExtractor = WebhookPayloadExtractor.newInstance();
        return new HandlerRouter(eventClassifier, payloadExtractor);
    }

    public HandlerRouter(WebhookEventClassifier eventClassifier, WebhookPayloadExtractor eventExtractor) {
        this.eventClassifier = eventClassifier;
        this.eventExtractor = eventExtractor;
    }

    public void route(GithubWebhookEvent event) {

        // ?? @RUI who extracts the payload, Extractors here or inside each handler?
        // probably inside each handler 'cause this guy should only route

        EventType eventType = eventClassifier.classify(event);
        EventHandler eventHandler = HANDLERS.get(eventType);

        if (eventHandler == null) {
            throw new IllegalStateException("Could not find correct handler for the event.");
        }

        //eventHandler.handle(event);
        eventHandler.handle(event); // FIXME eventHandler.handle(eventExtractor.extract(event)) does not produce compiler error and "should"!
    }

}
