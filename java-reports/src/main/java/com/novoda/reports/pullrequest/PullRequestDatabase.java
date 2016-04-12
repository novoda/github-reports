package com.novoda.reports.pullrequest;

import com.novoda.reports.organisation.OrganisationRepo;

import java.util.List;

public interface PullRequestDatabase {
    void open() throws DatabaseException;

    void create() throws DatabaseException;

    List<LitePullRequest> read(OrganisationRepo repo) throws DatabaseException;

    // nullable TODO add annotations
    FullPullRequest read(LitePullRequest litePullRequest) throws DatabaseException;

    void update(OrganisationRepo repo, List<LitePullRequest> litePullRequests) throws DatabaseException;

    void update(LitePullRequest litePullRequest, FullPullRequest fullPullRequest) throws DatabaseException;

    void close();

    class DatabaseException extends RuntimeException {

        public DatabaseException() {
            super();
        }

        public DatabaseException(String message) {
            super(message);
        }

        public DatabaseException(String message, Throwable cause) {
            super(message, cause);
        }

        public DatabaseException(Throwable cause) {
            super(cause);
        }
    }
}
