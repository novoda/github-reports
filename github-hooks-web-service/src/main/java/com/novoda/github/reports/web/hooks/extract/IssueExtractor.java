package com.novoda.github.reports.web.hooks.extract;

import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.service.repository.GithubRepository;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;
import com.novoda.github.reports.web.hooks.model.Issue;

public class IssueExtractor implements PayloadExtractor<Issue> {
    @Override
    public Issue extractFrom(GithubWebhookEvent event) throws ExtractException {
        GithubIssue issue = event.issue();
        GithubRepository repository = event.repository();
        if (issue == null) {
            throw new ExtractException(event);
        }
        return new Issue(issue, repository, event.action());
    }
}
