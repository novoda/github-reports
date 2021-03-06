package com.novoda.github.reports.service.pullrequest;

import com.google.gson.annotations.SerializedName;

public class GithubPullRequest {

    @SerializedName("merged")
    private boolean wasMerged;

    public GithubPullRequest(boolean wasMerged) {
        this.wasMerged = wasMerged;
    }

    public boolean isMerged() {
        return wasMerged;
    }
}
