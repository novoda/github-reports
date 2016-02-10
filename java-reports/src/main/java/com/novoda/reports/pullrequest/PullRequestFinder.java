package com.novoda.reports.pullrequest;

import com.novoda.reports.organisation.OrganisationRepo;
import org.eclipse.egit.github.core.service.PullRequestService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PullRequestFinder {

    private final PullRequestInMemoryDataSource inMemoryDataSource;
    private final PullRequestPersistenceDataSource persistenceDataSource;
    private final PullRequestWebServiceDataSource webServiceDataSource;

    public static PullRequestFinder newInstance(PullRequestService pullRequestService) {
        PullRequestInMemoryDataSource inMemoryDataSource = new PullRequestInMemoryDataSource();
        PullRequestPersistenceDataSource persistenceDataSource = new PullRequestPersistenceDataSource();
        PullRequestWebServiceDataSource.Converter converter = new PullRequestWebServiceDataSource.Converter();
        PullRequestWebServiceDataSource webServiceDataSource = new PullRequestWebServiceDataSource(pullRequestService, converter);
        return new PullRequestFinder(inMemoryDataSource, persistenceDataSource, webServiceDataSource);
    }

    PullRequestFinder(PullRequestInMemoryDataSource inMemoryDataSource,
                      PullRequestPersistenceDataSource persistenceDataSource,
                      PullRequestWebServiceDataSource webServiceDataSource) {
        this.inMemoryDataSource = inMemoryDataSource;
        this.persistenceDataSource = persistenceDataSource;
        this.webServiceDataSource = webServiceDataSource;
    }

    public Stream<LitePullRequest> getAllPullRequestsIn(List<OrganisationRepo> repos) {
        return repos
                .stream()
                .flatMap(repo -> getPullRequests(repo).stream());
    }

    private List<LitePullRequest> getPullRequests(OrganisationRepo repo) {
        List<LitePullRequest> inMemoryRepositories = inMemoryDataSource.readPullRequests(repo);
        if (!inMemoryRepositories.isEmpty()) {
            return inMemoryRepositories;
        }
        List<LitePullRequest> diskRepositories = persistenceDataSource.readPullRequests(repo);
        if (!diskRepositories.isEmpty()) {
            inMemoryDataSource.createPullRequests(repo, diskRepositories);
            return diskRepositories;
        }
        List<LitePullRequest> webRepositories = webServiceDataSource.readPullRequests(repo);
        persistenceDataSource.createPullRequests(repo, webRepositories);
        inMemoryDataSource.createPullRequests(repo, webRepositories);
        return webRepositories;
    }

    private static class PullRequestPersistenceDataSource {

        public void createPullRequests(OrganisationRepo repo, List<LitePullRequest> litePullRequests) {
            // TODO
        }

        public List<LitePullRequest> readPullRequests(OrganisationRepo repo) {
            // TODO
            return Collections.emptyList();
        }

    }

    private static class PullRequestWebServiceDataSource {

        private final PullRequestService pullRequestService;
        private final Converter converter;

        private PullRequestWebServiceDataSource(PullRequestService pullRequestService, Converter converter) {
            this.pullRequestService = pullRequestService;
            this.converter = converter;
        }

        public void createPullRequests(OrganisationRepo repo, List<LitePullRequest> litePullRequests) {
            throw new IllegalStateException("Not supported in this app.");
        }

        public List<LitePullRequest> readPullRequests(OrganisationRepo repo) {
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

}
