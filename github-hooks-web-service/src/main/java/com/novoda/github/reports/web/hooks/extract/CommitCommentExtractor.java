package com.novoda.github.reports.web.hooks.extract;

import com.novoda.github.reports.service.issue.GithubComment;
import com.novoda.github.reports.service.repository.GithubRepository;
import com.novoda.github.reports.web.hooks.model.CommitComment;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;

public class CommitCommentExtractor implements PayloadExtractor<CommitComment> {
    @Override
    public CommitComment extractFrom(GithubWebhookEvent event) throws ExtractException {
        GithubComment comment = event.comment();
        GithubRepository repository = event.repository();
        if (comment == null || repository == null) {
            throw new ExtractException(event);
        }
        return new CommitComment(comment, repository, event.action());
    }
}
