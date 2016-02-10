package com.novoda.reports.pullrequest;

import com.novoda.reports.organisation.OrganisationRepo;
import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.service.PullRequestService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

class PullRequestWebServiceDataSource {

    private final PullRequestService pullRequestService;
    private final LiteConverter liteConverter;
    private final FullConverter fullConverter;

    PullRequestWebServiceDataSource(PullRequestService pullRequestService,
                                    LiteConverter liteConverter, FullConverter fullConverter) {
        this.pullRequestService = pullRequestService;
        this.liteConverter = liteConverter;
        this.fullConverter = fullConverter;
    }

    public void createLitePullRequests(OrganisationRepo repo, List<LitePullRequest> litePullRequests) {
        throw new IllegalStateException("Not supported in this app.");
    }

    public List<LitePullRequest> readLitePullRequests(OrganisationRepo repo) {
        try {
            return pullRequestService
                    .getPullRequests(repo::getId, "all")
                    .stream()
                    .map(liteConverter::convert)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            String repoName = repo.getName();
            throw new IllegalStateException("Failed to get PRs for repo " + repoName, e);
        }
    }

    public FullPullRequest readFullPullRequests(LitePullRequest litePullRequest) {
        try {
            PullRequest pullRequest = pullRequestService.getPullRequest(litePullRequest::generateId, litePullRequest.getNumber());
            return fullConverter.convert(pullRequest);
        } catch (IOException e) {
            String repoName = litePullRequest.getRepoName();
            String title = litePullRequest.getTitle();
            throw new IllegalStateException("Failed to get full PR for repo " + repoName + " pr " + title, e);
        }
    }

}
