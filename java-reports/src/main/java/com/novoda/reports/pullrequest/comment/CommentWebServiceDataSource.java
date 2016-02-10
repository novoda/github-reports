package com.novoda.reports.pullrequest.comment;

import com.novoda.reports.pullrequest.LitePullRequest;
import org.eclipse.egit.github.core.service.PullRequestService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

class CommentWebServiceDataSource {

    private final PullRequestService pullRequestService;
    private final CommentConverter converter;

    CommentWebServiceDataSource(PullRequestService pullRequestService, CommentConverter converter) {
        this.pullRequestService = pullRequestService;
        this.converter = converter;
    }

    public List<Comment> readComments(LitePullRequest pullRequest) {
        try {
            return pullRequestService.getComments(pullRequest::generateId, pullRequest.getNumber())
                    .stream()
                    .map(converter::convert)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            String repoName = pullRequest.getRepoName();
            String title = pullRequest.getTitle();
            throw new IllegalStateException("FooBar for repo " + repoName + ", pr " + title, e);
        }
    }
}
