package com.novoda.github.reports.data.model;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class User {

    public static User create(Integer id, String username) {
        return new AutoValue_User(id, username);
    }

    public abstract Integer id();

    public abstract String username();

}
