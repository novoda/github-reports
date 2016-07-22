package com.novoda.github.reports.web.hooks.extract;

import com.novoda.github.reports.web.hooks.EventType;
import com.novoda.github.reports.web.hooks.lambda.GithubWebhookEvent;
import com.novoda.github.reports.web.hooks.parse.WebhookEventClassifier;

import java.util.HashMap;
import java.util.Map;

public class WebhookPayloadExtractor {

    private static final Map<EventType, PayloadExtractor> EXTRACTORS = new HashMap<>(5); // TODO unmodifiable?
    static {
        EXTRACTORS.put(EventType.COMMIT_COMMENT, new PullRequestExtractor());   // TODO proper handler
        EXTRACTORS.put(EventType.ISSUE, new PullRequestExtractor());            // TODO proper handler
        EXTRACTORS.put(EventType.PULL_REQUEST, new PullRequestExtractor());
        EXTRACTORS.put(EventType.ISSUE_COMMENT, new PullRequestExtractor());    // TODO proper handler
        EXTRACTORS.put(EventType.REVIEW_COMMENT, new PullRequestExtractor());   // TODO proper handler
    }

    private WebhookEventClassifier eventClassifier;

    public static WebhookPayloadExtractor newInstance() {
        WebhookEventClassifier eventClassifier = new WebhookEventClassifier();
        return new WebhookPayloadExtractor(eventClassifier);
    }

    WebhookPayloadExtractor(WebhookEventClassifier eventClassifier) {
        this.eventClassifier = eventClassifier;
    }

    public <T> T extract(GithubWebhookEvent event) {
        PayloadExtractor<T> payloadExtractor = EXTRACTORS.get(eventClassifier.classify(event));
        return payloadExtractor.extractFrom(event);
    }

}
