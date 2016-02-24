package com.novoda.reports.pullrequest.comment;

import com.novoda.reports.RateLimitRetryer;
import com.novoda.reports.pullrequest.LitePullRequest;
import org.eclipse.egit.github.core.CommitComment;
import org.eclipse.egit.github.core.client.NoSuchPageException;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.service.PullRequestService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

class CommentWebServiceDataSource {

    private final PullRequestService pullRequestService;
    private final CommentConverter converter;
    private final RateLimitRetryer rateLimitRetryer;

    CommentWebServiceDataSource(PullRequestService pullRequestService, CommentConverter converter, RateLimitRetryer rateLimitRetryer) {
        this.pullRequestService = pullRequestService;
        this.converter = converter;
        this.rateLimitRetryer = rateLimitRetryer;
    }

    public List<Comment> readComments(LitePullRequest pullRequest) {
        return readComments(pullRequest, new ArrayList<>(), 1);
    }

    private List<Comment> readComments(LitePullRequest pullRequest, List<CommitComment> elements, int page) {
        try {
            try {
                PageIterator<CommitComment> iterator = pullRequestService.pageComments(pullRequest::generateId, pullRequest.getNumber());
                while (iterator.hasNext()) {
                    Collection<CommitComment> next = iterator.next();
                    elements.addAll(next);
                    page++;
                }
            } catch (NoSuchPageException pageException) {
                IOException cause = pageException.getCause();
                rateLimitRetryer.checkRateLimitAndRetry(pullRequest, elements, page, this::readComments);
                throw cause;
            }

            return elements
                    .stream()
                    .map(converter::convert)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            String repoName = pullRequest.getRepoName();
            int number = pullRequest.getNumber();
            throw new IllegalStateException("Failed getting comments for repo " + repoName + ", pr " + number, e);
        }
    }
}
