package com.novoda.github.reports.service.issue;

import com.google.gson.annotations.SerializedName;
import com.novoda.github.reports.service.GithubUser;
import com.novoda.github.reports.service.pullrequest.GithubPullRequest;

import java.util.Date;

public class GithubIssue {

    private long id;

    private int number;

    private String title;

    private GithubUser user;

    private State state;

    private String url;

    private int comments;

    @SerializedName("created_at")
    private Date createdAt;

    @SerializedName("updated_at")
    private Date updatedAt;

    @SerializedName("closed_at")
    private Date closedAt;

    @SerializedName("pull_request")
    private GithubPullRequest pullRequest;

    public GithubIssue(int issueNumber, long ownerId, boolean isPullRequest) {
        this.number = issueNumber;
        this.user = new GithubUser(ownerId);
        if (isPullRequest) {
            this.pullRequest = new GithubPullRequest(false);
        }
    }

    public GithubIssue(long id, Date updatedAt, GithubUser user, GithubPullRequest pullRequest) {
        this.id = id;
        this.updatedAt = updatedAt;
        this.user = user;
        this.pullRequest = pullRequest;
    }

    public long getId() {
        return id;
    }

    public int getNumber() {
        return number;
    }

    public String getTitle() {
        return title;
    }

    public GithubUser getUser() {
        return user;
    }

    public Long getUserId() {
        return user.getId();
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public Date getClosedAt() {
        return closedAt;
    }

    public State getState() {
        return state;
    }

    public String getUrl() {
        return url;
    }

    public int getComments() {
        return comments;
    }

    public GithubPullRequest getPullRequest() {
        return pullRequest;
    }

    public boolean isPullRequest() {
        return pullRequest != null;
    }

    public void setIsPullRequest(boolean isPullRequest) {
        if (isPullRequest) {
            pullRequest = new GithubPullRequest(false);
        } else {
            pullRequest = null;
        }
    }

    @Override
    public String toString() {
        return String.format("%s (%d) [%s] - %s , %s", title, id, state, user.getUsername(), updatedAt);
    }

    public enum State {

        @SerializedName("open")
        OPEN("open"),

        @SerializedName("closed")
        CLOSED("closed"),

        @SerializedName("all")
        ALL("all");

        private final String state;

        State(String state) {
            this.state = state;
        }

        @Override
        public String toString() {
            return state;
        }
    }

}
