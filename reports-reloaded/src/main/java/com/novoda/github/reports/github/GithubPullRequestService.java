package com.novoda.github.reports.github;

import org.eclipse.egit.github.core.CommitComment;
import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.service.PullRequestService;

import java.util.List;

public class GithubPullRequestService {

    private PullRequestService pullRequestService;
    private IRepositoryIdProvider repositoryName;

    public static GithubPullRequestService newInstance(String repo) {
        RepositoryName repositoryName = new RepositoryName(repo);
        PullRequestService pullRequestService = new PullRequestService(ClientContainer.INSTANCE.getClient());
        return new GithubPullRequestService(pullRequestService, repositoryName);
    }

    GithubPullRequestService(PullRequestService pullRequestService, IRepositoryIdProvider repositoryName) {
        this.pullRequestService = pullRequestService;
        this.repositoryName = repositoryName;
    }

    public List<CommitComment> getComments(int pullRequestId) {
        return pullRequestService.getComments(repositoryName, pullRequestId);
    }
}
