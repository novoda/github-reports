package com.novoda.reports.pullrequest;

import com.almworks.sqlite4java.SQLiteException;
import com.novoda.reports.organisation.OrganisationRepo;

import java.util.List;

class PullRequestPersistenceDataSource {

    private final PullRequestSqlite3Database pullRequestDatabase;

    PullRequestPersistenceDataSource(PullRequestSqlite3Database pullRequestDatabase) {
        this.pullRequestDatabase = pullRequestDatabase;
    }

    public void createLitePullRequests(OrganisationRepo repo, List<LitePullRequest> litePullRequests) {
        try {
            pullRequestDatabase.open();
            pullRequestDatabase.create();
            pullRequestDatabase.update(repo, litePullRequests);
        } catch (SQLiteException e) {
            throw new IllegalStateException("Could not save lite pull requests to repository.", e);
        } finally {
            pullRequestDatabase.close();
        }
    }

    public List<LitePullRequest> readLitePullRequests(OrganisationRepo repo) {
        try {
            pullRequestDatabase.open();
            return pullRequestDatabase.read(repo);
        } catch (SQLiteException e) {
            throw new IllegalStateException("Could not read lite pull requests from repository.", e);
        } finally {
            pullRequestDatabase.close();
        }
    }

    public FullPullRequest readFullPullRequest(LitePullRequest litePullRequest) {
        try {
            pullRequestDatabase.open();
            return pullRequestDatabase.read(litePullRequest);
        } catch (SQLiteException e) {
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
        } catch (SQLiteException e) {
            throw new IllegalStateException("Could not save lite pull requests to repository.", e);
        } finally {
            pullRequestDatabase.close();
        }
    }
}
