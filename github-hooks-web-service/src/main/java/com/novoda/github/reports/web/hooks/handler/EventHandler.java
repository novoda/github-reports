package com.novoda.github.reports.web.hooks.handler;

import com.novoda.github.reports.web.hooks.lambda.GithubWebhookEvent;

interface EventHandler {

    boolean handle(GithubWebhookEvent event) throws UnhandledEventException;

}
