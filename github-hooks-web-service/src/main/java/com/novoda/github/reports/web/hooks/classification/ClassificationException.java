package com.novoda.github.reports.web.hooks.classification;

import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;

public class ClassificationException extends Exception {
    ClassificationException(GithubWebhookEvent event) {
        super("Unable to classify event: " + event.toString());
    }
}
