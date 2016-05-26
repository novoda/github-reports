package com.novoda.github.reports.data.model;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Repository {

    public static Repository create(Integer id, String name, boolean isPrivate) {
        return new AutoValue_Repository(id, name, isPrivate);
    }

    public abstract Integer id();

    public abstract String name();

    public abstract boolean isPrivate();

}
