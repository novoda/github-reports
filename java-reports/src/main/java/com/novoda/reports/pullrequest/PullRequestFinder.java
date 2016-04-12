package com.novoda.reports.pullrequest;

import com.novoda.reports.RateLimitRetryer;
import com.novoda.reports.organisation.OrganisationRepo;
import org.eclipse.egit.github.core.service.PullRequestService;

import java.util.List;
import java.util.stream.Stream;

public class PullRequestFinder {

    private final PullRequestInMemoryDataSource inMemoryDataSource;
    private final PullRequestPersistenceDataSource persistenceDataSource;
    private final PullRequestWebServiceDataSource webServiceDataSource;

    public static PullRequestFinder newInstance(PullRequestService pullRequestService, RateLimitRetryer rateLimitRetryer) {
        PullRequestInMemoryDataSource inMemoryDataSource = new PullRequestInMemoryDataSource();
        PullRequestDatabase pullRequestDatabase = new Sqlite3PullRequestDatabase();
        PullRequestPersistenceDataSource persistenceDataSource = new PullRequestPersistenceDataSource(pullRequestDatabase);
        LiteConverter liteConverter = new LiteConverter();
        FullConverter fullConverter = new FullConverter(liteConverter);
        PullRequestWebServiceDataSource webServiceDataSource = new PullRequestWebServiceDataSource(pullRequestService, liteConverter, fullConverter, rateLimitRetryer);
        return new PullRequestFinder(inMemoryDataSource, persistenceDataSource, webServiceDataSource);
    }

    PullRequestFinder(PullRequestInMemoryDataSource inMemoryDataSource,
                      PullRequestPersistenceDataSource persistenceDataSource,
                      PullRequestWebServiceDataSource webServiceDataSource) {
        this.inMemoryDataSource = inMemoryDataSource;
        this.persistenceDataSource = persistenceDataSource;
        this.webServiceDataSource = webServiceDataSource;
    }

    public Stream<LitePullRequest> streamLitePullRequests(OrganisationRepo repo) {
        return getLitePullRequests(repo).stream();
    }

    public List<LitePullRequest> getLitePullRequests(OrganisationRepo repo) {
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
        FullPullRequest inMemoryRepositories = inMemoryDataSource.readFullPullRequest(litePullRequest);
        if (inMemoryRepositories != null) {
            return inMemoryRepositories;
        }
        FullPullRequest diskRepositories = persistenceDataSource.readFullPullRequest(litePullRequest);
        if (diskRepositories != null) {
            inMemoryDataSource.createFullPullRequest(litePullRequest, diskRepositories);
            return diskRepositories;
        }
        FullPullRequest fullPullRequest = webServiceDataSource.readFullPullRequest(litePullRequest);
        persistenceDataSource.createFullPullRequest(litePullRequest, fullPullRequest);
        inMemoryDataSource.createFullPullRequest(litePullRequest, fullPullRequest);
        return fullPullRequest;
    }

}
