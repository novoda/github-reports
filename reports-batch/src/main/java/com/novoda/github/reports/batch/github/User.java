package com.novoda.github.reports.batch.github;

import com.google.gson.annotations.SerializedName;

public class User {

    private int id;

    @SerializedName(value = "login", alternate = {"name"}) // "name" as an alternative due to compatibility issues with the Timeline API
    private String username;

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}
