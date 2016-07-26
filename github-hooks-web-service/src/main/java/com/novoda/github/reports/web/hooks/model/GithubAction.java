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
    PUBLISHED("published"),

    @SerializedName("assigned")
    ASSIGNED("assigned"),

    @SerializedName("unassigned")
    UNASSIGNED("unassigned"),

    @SerializedName("labeled")
    LABELED("labeled"),

    @SerializedName("unlabeled")
    UNLABELED("unlabeled"),

    @SerializedName("edited")
    EDITED("edited"),

    @SerializedName("repopened")
    REOPENED("reopened"),

    @SerializedName("synchronize")
    SYNCHRONIZE("synchronize");

    /*
    from https://developer.github.com/v3/activity/events/types/#pullrequestevent:
    "assigned", "unassigned", "labeled", "unlabeled", "opened", "edited", "closed", or "reopened", or "synchronize"
    */

    private final String action;

    GithubAction(String action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return action;
    }
}
