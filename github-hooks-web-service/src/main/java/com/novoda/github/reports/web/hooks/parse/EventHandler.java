package com.novoda.github.reports.web.hooks.parse;

import com.novoda.github.reports.web.hooks.lambda.GithubWebhookEvent;

public interface EventHandler<T> {

    void handle(T t, GithubWebhookEvent.Action action); // @RUI should action be on its own file?

    //void handle(GithubWebhookEvent event);
}
