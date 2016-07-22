package com.novoda.github.reports.web.hooks.parse;

import com.novoda.github.reports.web.hooks.handler.UnhandledEventException;
import com.novoda.github.reports.web.hooks.lambda.GithubWebhookEvent;

public interface EventHandler {

    boolean handle(GithubWebhookEvent event) throws UnhandledEventException;

}
