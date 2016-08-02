package com.novoda.github.reports.web.hooks.extract;

import com.novoda.github.reports.service.issue.GithubComment;
import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;
import com.novoda.github.reports.web.hooks.model.IssueComment;

public class IssueCommentExtractor implements PayloadExtractor<IssueComment> {
    @Override
    public IssueComment extractFrom(GithubWebhookEvent event) throws ExtractException {
        GithubComment comment = event.comment();
        GithubIssue issue = event.issue();
        if (comment == null || issue == null) {
            throw new ExtractException(event);
        }
        return new IssueComment(issue, comment, event.action());
    }
}
