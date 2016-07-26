package com.novoda.github.reports.web.hooks.extract;

import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.service.repository.GithubRepository;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;
import com.novoda.github.reports.web.hooks.model.PullRequest;

public class PullRequestExtractor implements PayloadExtractor<PullRequest> {
    @Override
    public PullRequest extractFrom(GithubWebhookEvent event) throws ExtractException {
        GithubIssue issue = event.pullRequest();
        GithubRepository repository = event.repository();
        if (issue == null || repository == null) {
            throw new ExtractException(event);
        }
        issue.setIsPullRequest(true);
        return new PullRequest(issue, repository);
    }
}
