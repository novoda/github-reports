package com.novoda.github.reports.web.hooks.model;

import com.google.gson.annotations.SerializedName;
import com.novoda.github.reports.service.GithubUser;
import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.service.pullrequest.GithubPullRequest;

import java.util.Date;

public class GithubWebhookPullRequest extends GithubIssue {

    @SerializedName("merged")
    private boolean wasMerged;

    public GithubWebhookPullRequest(long id, Date updatedAt, GithubUser user, boolean wasMerged) {
        super(id, updatedAt, user, new GithubPullRequest(wasMerged));
    }

    @Override
    public boolean isPullRequest() {
        return true;
    }

    public boolean wasMerged() {
        return wasMerged;
    }
}
