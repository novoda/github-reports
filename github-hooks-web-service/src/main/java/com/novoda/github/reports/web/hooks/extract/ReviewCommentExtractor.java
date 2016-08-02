package com.novoda.github.reports.web.hooks.extract;

import com.novoda.github.reports.service.issue.GithubComment;
import com.novoda.github.reports.service.repository.GithubRepository;
import com.novoda.github.reports.web.hooks.model.ReviewComment;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;

public class ReviewCommentExtractor implements PayloadExtractor<ReviewComment> {
    @Override
    public ReviewComment extractFrom(GithubWebhookEvent event) throws ExtractException {
        GithubComment comment = event.comment();
        GithubRepository repository = event.repository();
        // TODO check if we want to extract the pull request object here too
        if (comment == null || repository == null) {
            throw new ExtractException(event);
        }
        return new ReviewComment(comment, repository, event.action());
    }
}
