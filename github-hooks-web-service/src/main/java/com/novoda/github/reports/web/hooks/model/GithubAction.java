package com.novoda.github.reports.web.hooks.model;

import com.google.gson.annotations.SerializedName;

public enum GithubAction {

    @SerializedName("opened")
    OPENED("opened"),

    @SerializedName("closed")
    CLOSED("closed"),

    @SerializedName("created")
    CREATED("created"),

    @SerializedName("added")
    ADDED("added"),

    @SerializedName("published")
    PUBLISHED("published");

    private final String action;

    GithubAction(String action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return action;
    }
}
