package com.novoda.reports.pullrequest;

import com.novoda.reports.organisation.OrganisationRepo;
import com.novoda.reports.pullrequest.PullRequestDatabase.DatabaseException;

import java.util.List;

class PullRequestPersistenceDataSource {

    private final PullRequestDatabase pullRequestDatabase;

    PullRequestPersistenceDataSource(PullRequestDatabase pullRequestDatabase) {
        this.pullRequestDatabase = pullRequestDatabase;
    }

    public void createLitePullRequests(OrganisationRepo repo, List<LitePullRequest> litePullRequests) {
        try {
            pullRequestDatabase.open();
            pullRequestDatabase.create();
            pullRequestDatabase.update(repo, litePullRequests);
        } catch (DatabaseException e) {
            throw new IllegalStateException("Could not save lite pull requests to repository.", e);
        } finally {
            pullRequestDatabase.close();
        }
    }

    public List<LitePullRequest> readLitePullRequests(OrganisationRepo repo) {
        try {
            pullRequestDatabase.open();
            return pullRequestDatabase.read(repo);
        } catch (DatabaseException e) {
            throw new IllegalStateException("Could not read lite pull requests from repository.", e);
        } finally {
            pullRequestDatabase.close();
        }
    }

    public FullPullRequest readFullPullRequest(LitePullRequest litePullRequest) {
        try {
            pullRequestDatabase.open();
            return pullRequestDatabase.read(litePullRequest);
        } catch (DatabaseException e) {
            throw new IllegalStateException("Could not read lite pull requests from repository.", e);
        } finally {
            pullRequestDatabase.close();
        }
    }

    public void createFullPullRequest(LitePullRequest litePullRequest, FullPullRequest fullPullRequest) {
        try {
            pullRequestDatabase.open();
            pullRequestDatabase.create();
            pullRequestDatabase.update(litePullRequest, fullPullRequest);
        } catch (DatabaseException e) {
            throw new IllegalStateException("Could not save lite pull requests to repository.", e);
        } finally {
            pullRequestDatabase.close();
        }
    }
}
