package com.novoda.github.reports.service.issue;

import com.google.gson.annotations.SerializedName;
import com.novoda.github.reports.service.GithubUser;

import java.util.Date;

public class GithubComment {

    private long id;

    private GithubUser user;

    @SerializedName("created_at")
    private Date createdAt;

    @SerializedName("updated_at")
    private Date updatedAt;

    public GithubComment(long id, GithubUser user, Date updatedAt) {
        this.id = id;
        this.user = user;
        this.updatedAt = updatedAt;
    }

    public long getId() {
        return id;
    }

    public GithubUser getUser() {
        return user;
    }

    public long getUserId() {
        return user.getId();
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return String.format("%s commented [%d] @ %s", user.getUsername(), id, createdAt);
    }
}
