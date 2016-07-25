package com.novoda.github.reports.web.hooks.extract;

import com.novoda.github.reports.service.issue.GithubComment;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;

public class CommitCommentExtractor implements PayloadExtractor<GithubComment> {
    @Override
    public GithubComment extractFrom(GithubWebhookEvent event) throws ExtractException {
        GithubComment comment = event.comment();
        if (comment == null) {
            throw new ExtractException(event);
        }
        return comment;
    }
}
