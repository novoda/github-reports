package com.novoda.github.reports.web.hooks.extract;

import com.novoda.github.reports.service.issue.GithubComment;
import com.novoda.github.reports.service.repository.GithubRepository;
import com.novoda.github.reports.web.hooks.model.CommitComment;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;

public class CommitCommentExtractor implements PayloadExtractor<CommitComment> {
    @Override
    public CommitComment extractFrom(GithubWebhookEvent event) throws ExtractException {
        GithubComment githubComment = event.comment();
        GithubRepository githubRepository = event.repository();
        if (githubComment == null || githubRepository == null) {
            throw new ExtractException(event);
        }
        return new CommitComment(githubComment, githubRepository, event.action());
    }
}
