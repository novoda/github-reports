package com.novoda.github.reports.web.hooks.parse;

import com.novoda.github.reports.web.hooks.lambda.GithubWebhookEvent;

import java.util.Optional;

interface WebhookEventParser<T> {

    Optional<T> from(GithubWebhookEvent event);

}
