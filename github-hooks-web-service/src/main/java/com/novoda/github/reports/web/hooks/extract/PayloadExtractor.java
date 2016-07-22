package com.novoda.github.reports.web.hooks.extract;

import com.novoda.github.reports.web.hooks.lambda.GithubWebhookEvent;

interface PayloadExtractor<T> {

    T extractFrom(GithubWebhookEvent event);

}
