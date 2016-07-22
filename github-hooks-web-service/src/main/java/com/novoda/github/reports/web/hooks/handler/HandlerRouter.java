package com.novoda.github.reports.web.hooks.handler;

import com.novoda.github.reports.web.hooks.EventType;
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

    HandlerRouter(WebhookEventClassifier eventClassifier) {
        this.eventClassifier = eventClassifier;
    }

    public void route(GithubWebhookEvent event) {

        // TODO classify and route accordingly

        // ?? @RUI who extracts the payload, Extractors here or inside each handler?
        // probably inside each handler 'cause this guy should only route

    }

}
