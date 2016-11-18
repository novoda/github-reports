package com.novoda.github.reports.web.hooks.extract;

import com.novoda.github.reports.service.issue.GithubComment;
import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.service.repository.GithubRepository;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;
import com.novoda.github.reports.web.hooks.model.IssueComment;

public class IssueCommentExtractor implements PayloadExtractor<IssueComment> {
    @Override
    public IssueComment extractFrom(GithubWebhookEvent event) throws ExtractException {
        GithubComment comment = event.comment();
        GithubRepository repository = event.repository();
        GithubIssue issue = event.issue();
        if (comment == null || issue == null || repository == null || issue.isPullRequest()) {
            throw new ExtractException(event);
        }
        return new IssueComment(comment, repository, issue, event.action());
    }
}
