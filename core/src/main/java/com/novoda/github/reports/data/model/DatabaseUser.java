package com.novoda.github.reports.data.model;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class DatabaseUser {

    public static DatabaseUser create(Long id, String username) {
        return new AutoValue_DatabaseUser(id, username);
    }

    public abstract Long id();

    public abstract String username();

}
