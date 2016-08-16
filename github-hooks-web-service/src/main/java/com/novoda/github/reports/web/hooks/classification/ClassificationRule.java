package com.novoda.github.reports.web.hooks.classification;

import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;

interface ClassificationRule {

    boolean check(GithubWebhookEvent event);

}
