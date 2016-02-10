package com.novoda.reports.pullrequest;

import com.novoda.reports.organisation.OrganisationRepo;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class PullRequestInMemoryDataSource {

    private static final Map<OrganisationRepo, List<LitePullRequest>> CACHE = new HashMap<>();

    public void createPullRequests(OrganisationRepo repo, List<LitePullRequest> litePullRequests) {
        CACHE.put(repo, litePullRequests);
    }

    public List<LitePullRequest> readPullRequests(OrganisationRepo repo) {
        if (CACHE.containsKey(repo)) {
            return CACHE.get(repo);
        } else {
            return Collections.emptyList();
        }
    }

}
