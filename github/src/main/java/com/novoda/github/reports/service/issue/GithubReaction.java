package com.novoda.github.reports.service.issue;

import com.google.gson.annotations.SerializedName;
import com.novoda.github.reports.service.GithubUser;

import java.util.Date;

public class GithubReaction {

    private long id;

    private GithubUser user;

    @SerializedName("created_at")
    private Date createdAt;

    public GithubReaction(long id, GithubUser user, Date createdAt) {
        this.id = id;
        this.user = user;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public GithubUser getUser() {
        return user;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}
