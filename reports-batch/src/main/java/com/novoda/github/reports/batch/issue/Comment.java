package com.novoda.github.reports.batch.issue;

import com.google.gson.annotations.SerializedName;
import com.novoda.github.reports.batch.User;

import java.util.Date;

public class Comment {

    private long id;

    private User user;

    @SerializedName("created_at")
    private Date createdAt;

    @SerializedName("updated_at")
    private Date updatedAt;

    public long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return String.format("%s commented [%d] @ %s", user.getUsername(), id, createdAt);
    }
}
