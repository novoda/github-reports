package com.novoda.github.reports.web.hooks.extract;

import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;

public class IssueExtractor implements PayloadExtractor<GithubIssue> {
    @Override
    public GithubIssue extractFrom(GithubWebhookEvent event) throws ExtractException {
        GithubIssue issue = event.issue();
        if (issue == null) {
            throw new ExtractException(event);
        }
        return issue;
    }
}
