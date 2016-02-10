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
        PullRequestWebServiceDataSource.LiteConverter liteConverter = new PullRequestWebServiceDataSource.LiteConverter();
        PullRequestWebServiceDataSource.FullConverter fullConverter = new PullRequestWebServiceDataSource.FullConverter(liteConverter);
        PullRequestWebServiceDataSource webServiceDataSource = new PullRequestWebServiceDataSource(pullRequestService, liteConverter, fullConverter);
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
        List<LitePullRequest> litePullRequests = webServiceDataSource.readLitePullRequests(repo);
        persistenceDataSource.createLitePullRequests(repo, litePullRequests);
        inMemoryDataSource.createLitePullRequests(repo, litePullRequests);
        return litePullRequests;
    }

    public FullPullRequest getFullPullRequest(LitePullRequest litePullRequest) {
        FullPullRequest inMemoryRepositories = inMemoryDataSource.readFullPullRequests(litePullRequest);
        if (inMemoryRepositories != null) {
            return inMemoryRepositories;
        }
        FullPullRequest diskRepositories = persistenceDataSource.readFullPullRequests(litePullRequest);
        if (diskRepositories != null) {
            inMemoryDataSource.createFullPullRequests(litePullRequest, diskRepositories);
            return diskRepositories;
        }
        FullPullRequest fullPullRequest = webServiceDataSource.readFullPullRequests(litePullRequest);
        persistenceDataSource.createFullPullRequests(litePullRequest, fullPullRequest);
        inMemoryDataSource.createFullPullRequests(litePullRequest, fullPullRequest);
        return fullPullRequest;
    }

}
