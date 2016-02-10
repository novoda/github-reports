package com.novoda.reports.pullrequest;

import com.novoda.reports.organisation.OrganisationRepo;
import org.eclipse.egit.github.core.service.PullRequestService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

class PullRequestWebServiceDataSource {

    private final PullRequestService pullRequestService;
    private final Converter converter;

    PullRequestWebServiceDataSource(PullRequestService pullRequestService, Converter converter) {
        this.pullRequestService = pullRequestService;
        this.converter = converter;
    }

    public void createLitePullRequests(OrganisationRepo repo, List<LitePullRequest> litePullRequests) {
        throw new IllegalStateException("Not supported in this app.");
    }

    public List<LitePullRequest> readLitePullRequests(OrganisationRepo repo) {
        try {
            return pullRequestService
                    .getPullRequests(repo::getId, "all")
                    .stream()
                    .map(converter::convert)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            String repoName = repo.getName();
            throw new IllegalStateException("Failed to get PRs for repo " + repoName, e);
        }
    }

    static class Converter {

        public LitePullRequest convert(org.eclipse.egit.github.core.PullRequest pullRequest) {
            String repoName = pullRequest.getBase().getRepo().getName();
            String repoOwnerLogin = pullRequest.getBase().getRepo().getOwner().getLogin();
            int number = pullRequest.getNumber();
            String title = pullRequest.getTitle();
            String userLogin = pullRequest.getUser().getLogin();
            LocalDate createdAt = convertToLocalDate(pullRequest.getCreatedAt());
            return new LitePullRequest(repoName, repoOwnerLogin,
                    number, title,
                    userLogin,
                    createdAt);
        }

        private LocalDate convertToLocalDate(Date java7Date) {
            return java7Date
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }

    }
}
