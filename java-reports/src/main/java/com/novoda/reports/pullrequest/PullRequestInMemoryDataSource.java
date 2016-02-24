package com.novoda.reports.pullrequest;

import com.novoda.reports.organisation.OrganisationRepo;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class PullRequestInMemoryDataSource {

    private static final Map<OrganisationRepo, List<LitePullRequest>> LITE_CACHE = new HashMap<>();
    private static final Map<LitePullRequest, FullPullRequest> FULL_CACHE = new HashMap<>();

    public void createLitePullRequests(OrganisationRepo repo, List<LitePullRequest> litePullRequests) {
        LITE_CACHE.put(repo, litePullRequests);
    }

    public List<LitePullRequest> readLitePullRequests(OrganisationRepo repo) {
        if (LITE_CACHE.containsKey(repo)) {
            return LITE_CACHE.get(repo);
        } else {
            return Collections.emptyList();
        }
    }

    public void createFullPullRequest(LitePullRequest litePullRequest, FullPullRequest fullPullRequest) {
        FULL_CACHE.put(litePullRequest, fullPullRequest);
    }

    // Nullable TODO introduce annotation
    public FullPullRequest readFullPullRequest(LitePullRequest litePullRequest) {
        if (FULL_CACHE.containsKey(litePullRequest)) {
            return FULL_CACHE.get(litePullRequest);
        } else {
            return null;
        }
    }
}
