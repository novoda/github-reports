package com.novoda.github.reports.github;

import org.eclipse.egit.github.core.CommitComment;
import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.service.PullRequestService;

import java.io.IOException;
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

    public List<CommitComment> getComments(PullRequest pullRequest) {
        return getComments((int) pullRequest.getId()); // FIXME strange api...
    }

    public List<CommitComment> getComments(int pullRequestId) {
        try {
            return pullRequestService.getComments(repositoryName, pullRequestId);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<PullRequest> getPullRequests(State state) {
        try {
            return pullRequestService.getPullRequests(repositoryName, state.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
