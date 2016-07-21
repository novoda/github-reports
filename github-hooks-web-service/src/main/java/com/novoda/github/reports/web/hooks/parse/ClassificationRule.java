package com.novoda.github.reports.web.hooks.parse;

import com.novoda.github.reports.web.hooks.lambda.GithubWebhookEvent;

interface ClassificationRule {

    boolean check(GithubWebhookEvent event);

}
