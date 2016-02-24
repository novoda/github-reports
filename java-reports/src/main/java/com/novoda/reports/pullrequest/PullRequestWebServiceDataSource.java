package com.novoda.reports.pullrequest;

import com.novoda.reports.RateLimitRetryer;
import com.novoda.reports.organisation.OrganisationRepo;
import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.client.NoSuchPageException;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.service.PullRequestService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

class PullRequestWebServiceDataSource {

    private final PullRequestService pullRequestService;
    private final LiteConverter liteConverter;
    private final FullConverter fullConverter;
    private final RateLimitRetryer rateLimitRetryer;

    PullRequestWebServiceDataSource(PullRequestService pullRequestService,
                                    LiteConverter liteConverter, FullConverter fullConverter, RateLimitRetryer rateLimitRetryer) {
        this.pullRequestService = pullRequestService;
        this.liteConverter = liteConverter;
        this.fullConverter = fullConverter;
        this.rateLimitRetryer = rateLimitRetryer;
    }

    public void createLitePullRequests(OrganisationRepo repo, List<LitePullRequest> litePullRequests) {
        throw new IllegalStateException("Not supported in this app.");
    }

    public List<LitePullRequest> readLitePullRequests(OrganisationRepo repo) {
        return readLitePullRequests(repo, new ArrayList<>(), 1);
    }

    private List<LitePullRequest> readLitePullRequests(OrganisationRepo repo, List<PullRequest> elements, int page) {
        try {
            try {
                PageIterator<PullRequest> iterator = pullRequestService.pagePullRequests(repo::getId, "all");
                while (iterator.hasNext()) {
                    Collection<PullRequest> next = iterator.next();
                    elements.addAll(next);
                    page++;
                }
            } catch (NoSuchPageException pageException) {
                IOException cause = pageException.getCause();
                rateLimitRetryer.checkRateLimitAndRetry(repo, elements, page, this::readLitePullRequests);
                throw cause;
            }

            return elements
                    .stream()
                    .map(liteConverter::convert)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            String repoName = repo.getName();
            throw new IllegalStateException("Failed to get PRs for repo " + repoName, e);
        }
    }

    public FullPullRequest readFullPullRequest(LitePullRequest litePullRequest) {
        try {
            PullRequest pullRequest = pullRequestService.getPullRequest(litePullRequest::generateId, litePullRequest.getNumber());
            return fullConverter.convert(pullRequest);
        } catch (IOException e) {
            rateLimitRetryer.checkRateLimitAndRetry(litePullRequest, this::readFullPullRequest);
            String repoName = litePullRequest.getRepoName();
            String title = litePullRequest.getTitle();
            throw new IllegalStateException("Failed to get full PR for repo " + repoName + " pr " + title, e);
        }
    }

}
