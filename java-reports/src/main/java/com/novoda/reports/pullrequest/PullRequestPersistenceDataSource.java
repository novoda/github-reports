package com.novoda.reports.pullrequest;

import com.novoda.reports.organisation.OrganisationRepo;

import java.util.Collections;
import java.util.List;

class PullRequestPersistenceDataSource {

    public void createLitePullRequests(OrganisationRepo repo, List<LitePullRequest> litePullRequests) {
        // TODO
    }

    public List<LitePullRequest> readLitePullRequests(OrganisationRepo repo) {
        // TODO
        return Collections.emptyList();
    }

    public FullPullRequest readFullPullRequests(LitePullRequest litePullRequest) {
        // TODO
        return null;
    }

    public void createFullPullRequests(LitePullRequest litePullRequest, FullPullRequest fullPullRequest) {
        // TODO
    }
}
