package com.novoda.github.reports.web.hooks.handler;

import com.novoda.github.reports.data.db.DbEventDataLayer;
import com.novoda.github.reports.web.hooks.lambda.GithubWebhookEvent;
import com.novoda.github.reports.web.hooks.parse.EventHandler;

class PullRequestHandler implements EventHandler {

    private DbEventDataLayer eventDataLayer;

    @Override
    public void handle(GithubWebhookEvent event) {
        // TODO persist and... ?
    }
}
