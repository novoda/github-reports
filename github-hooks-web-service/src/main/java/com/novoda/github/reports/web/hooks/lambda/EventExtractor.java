package com.novoda.github.reports.web.hooks.lambda;

import com.google.gson.Gson;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;
import com.novoda.github.reports.web.hooks.model.WebhookRequest;

import org.jetbrains.annotations.Nullable;

class EventExtractor {

    private final OutputWriter outputWriter;
    private final Gson gson;

    EventExtractor(OutputWriter outputWriter, Gson gson) {
        this.outputWriter = outputWriter;
        this.gson = gson;
    }

    @Nullable
    GithubWebhookEvent extractFrom(WebhookRequest request) throws RuntimeException {
        if (request.body() == null) {
            outputWriter.outputException(new NullPointerException("No event data found. Check Github's webhook delivery report."));
        }

        GithubWebhookEvent event = null;
        try {
            event = gson.fromJson(request.body(), GithubWebhookEvent.class);
        } catch (Exception e) {
            outputWriter.outputException(e);
        }

        return event;
    }

}
