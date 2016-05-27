package com.novoda.github.reports.github.issue;

import com.google.gson.annotations.SerializedName;
import com.novoda.github.reports.github.User;

import java.util.Date;

public class Comment {

    private long id;

    private User user;

    @SerializedName("created_at")
    private Date createdAt;

    @SerializedName("updated_at")
    private Date updatedAt;

    @Override
    public String toString() {
        return String.format("%s commented [%d] @ %s", user.getUsername(), id, createdAt);
    }
}
