package com.novoda.github.reports.web.hooks.classification;

import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;

import java.util.Arrays;
import java.util.function.Supplier;

public class WebhookEventClassifier {

    public EventType classify(GithubWebhookEvent event) throws ClassificationException {
        return Arrays
                .stream(EventType.values())
                .filter(eventType -> eventType.getRule().check(event))
                .findFirst()
                .orElseThrow(supplyClassificationExceptionFor(event));
    }

    private Supplier<ClassificationException> supplyClassificationExceptionFor(GithubWebhookEvent event) {
        return () -> new ClassificationException(event);
    }

}
