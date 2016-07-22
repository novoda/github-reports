package com.novoda.github.reports.web.hooks.extract;

import com.novoda.github.reports.web.hooks.lambda.GithubWebhookEvent;

public class ExtractException extends Exception {
    public ExtractException(GithubWebhookEvent event) {
        super("Unable to extract payload from event: " + event.toString());
    }
}
