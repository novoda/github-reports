package com.novoda.github.reports.web.hooks.parse;

import com.novoda.github.reports.web.hooks.lambda.GithubWebhookEvent;

public interface ClassificationRule {

    boolean checkFor(GithubWebhookEvent event);

}
