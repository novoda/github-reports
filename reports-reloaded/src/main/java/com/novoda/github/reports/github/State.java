package com.novoda.github.reports.github;

import com.google.gson.annotations.SerializedName;

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
