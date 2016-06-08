package com.novoda.github.reports.service;

import com.google.gson.annotations.SerializedName;

public class GithubUser {

    private long id;

    @SerializedName(value = "login", alternate = {"name"}) // "name" as an alternative due to compatibility issues with the Timeline API
    private String username;

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
