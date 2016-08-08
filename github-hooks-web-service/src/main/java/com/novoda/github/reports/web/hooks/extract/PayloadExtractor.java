package com.novoda.github.reports.web.hooks.extract;

import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;

public interface PayloadExtractor<T> {

    T extractFrom(GithubWebhookEvent event) throws ExtractException;

}
