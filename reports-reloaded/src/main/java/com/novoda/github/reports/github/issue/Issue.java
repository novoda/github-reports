package com.novoda.github.reports.github.issue;

import com.google.gson.annotations.SerializedName;
import com.novoda.github.reports.github.User;

import java.util.Date;

public class Issue {

    private long id;

    private int number;

    private String title;

    private User user;

    private String state;

    private String url;

    private int comments;

    @SerializedName("created_at")
    private Date createdAt;

    @SerializedName("updated_at")
    private Date updatedAt;

    @SerializedName("closed_at")
    private Date closedAt;

    public long getId() {
        return id;
    }

    public int getNumber() {
        return number;
    }

    public String getTitle() {
        return title;
    }

    public User getUser() {
        return user;
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

    public String getState() {
        return state;
    }

    public String getUrl() {
        return url;
    }

    public int getComments() {
        return comments;
    }

    @Override
    public String toString() {
        return String.format("%s (%d) - %s , %s", title, id, user.getUsername(), updatedAt);
    }
}
