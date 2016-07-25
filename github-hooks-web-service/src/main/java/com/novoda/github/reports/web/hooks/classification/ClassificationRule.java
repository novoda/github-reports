package com.novoda.github.reports.web.hooks.classification;

import com.novoda.github.reports.web.hooks.lambda.GithubWebhookEvent;

interface ClassificationRule {

    boolean check(GithubWebhookEvent event);

}
