package com.novoda.github.reports.web.hooks.extract;

import com.novoda.github.reports.service.issue.GithubComment;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;
import com.novoda.github.reports.web.hooks.model.GithubWebhookPullRequest;
import com.novoda.github.reports.web.hooks.model.CommitComment;

public class CommitCommentExtractor implements PayloadExtractor<CommitComment> {
    @Override
    public CommitComment extractFrom(GithubWebhookEvent event) throws ExtractException {
        GithubComment comment = event.comment();
        GithubWebhookPullRequest webhookPullRequest = event.pullRequest();
        if (comment == null || webhookPullRequest == null) {
            throw new ExtractException(event);
        }
        webhookPullRequest.setIsPullRequest(true);
        return new CommitComment(webhookPullRequest, comment);
    }
}
