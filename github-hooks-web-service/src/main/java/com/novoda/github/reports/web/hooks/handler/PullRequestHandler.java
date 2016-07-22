package com.novoda.github.reports.web.hooks.handler;

import com.novoda.github.reports.data.db.DbEventDataLayer;
import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.web.hooks.lambda.GithubWebhookEvent;
import com.novoda.github.reports.web.hooks.parse.EventHandler;

public class PullRequestHandler implements EventHandler<GithubIssue> {

    private DbEventDataLayer eventDataLayer;

    @Override
    public void handle(GithubIssue issue, GithubWebhookEvent.Action action) {
        // TODO persist and... ?
    }
}
