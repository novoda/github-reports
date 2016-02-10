package com.novoda.reports.pullrequest;

import com.novoda.reports.organisation.OrganisationRepo;
import org.eclipse.egit.github.core.service.PullRequestService;

import java.util.List;
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

    public Stream<LitePullRequest> getAllLitePullRequestsIn(List<OrganisationRepo> repos) {
        return repos
                .stream()
                .flatMap(repo -> getLitePullRequests(repo).stream());
    }

    private List<LitePullRequest> getLitePullRequests(OrganisationRepo repo) {
        List<LitePullRequest> inMemoryRepositories = inMemoryDataSource.readLitePullRequests(repo);
        if (!inMemoryRepositories.isEmpty()) {
            return inMemoryRepositories;
        }
        List<LitePullRequest> diskRepositories = persistenceDataSource.readLitePullRequests(repo);
        if (!diskRepositories.isEmpty()) {
            inMemoryDataSource.createLitePullRequests(repo, diskRepositories);
            return diskRepositories;
        }
        List<LitePullRequest> webRepositories = webServiceDataSource.readLitePullRequests(repo);
        persistenceDataSource.createLitePullRequests(repo, webRepositories);
        inMemoryDataSource.createLitePullRequests(repo, webRepositories);
        return webRepositories;
    }

}
