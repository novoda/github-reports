package com.novoda.github.reports.web.hooks.model;

import com.google.gson.annotations.SerializedName;
import com.novoda.github.reports.service.issue.GithubIssue;

public class GithubWebhookPullRequest extends GithubIssue {

    @SerializedName("merged")
    private boolean wasMerged;

    public GithubWebhookPullRequest(int issueNumber, long ownerId, boolean isPullRequest) {
        super(issueNumber, ownerId, isPullRequest);
    }

    @Override
    public boolean isPullRequest() {
        return true;
    }

    public boolean isWasMerged() {
        return wasMerged;
    }
}
