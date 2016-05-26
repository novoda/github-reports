package com.novoda.github.reports.github;

import com.google.gson.annotations.SerializedName;

public class User {

    private int id;

    @SerializedName(value = "login", alternate = {"name"})
    private String username;

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}
