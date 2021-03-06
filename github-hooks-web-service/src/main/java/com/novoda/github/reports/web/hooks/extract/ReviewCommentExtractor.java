package com.novoda.github.reports.web.hooks.extract;

import com.novoda.github.reports.service.issue.GithubComment;
import com.novoda.github.reports.service.repository.GithubRepository;
import com.novoda.github.reports.web.hooks.model.GithubWebhookPullRequest;
import com.novoda.github.reports.web.hooks.model.ReviewComment;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;

public class ReviewCommentExtractor implements PayloadExtractor<ReviewComment> {
    @Override
    public ReviewComment extractFrom(GithubWebhookEvent event) throws ExtractException {
        GithubComment comment = event.comment();
        GithubRepository repository = event.repository();
        GithubWebhookPullRequest webhookPullRequest = event.pullRequest();
        if (comment == null
                || repository == null
                || webhookPullRequest == null) {
            throw new ExtractException(event);
        }
        return new ReviewComment(comment, repository, webhookPullRequest, event.action());
    }
}
