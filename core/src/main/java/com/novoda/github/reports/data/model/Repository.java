package com.novoda.github.reports.data.model;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Repository {

    public static Repository create(Long id, String name, boolean isPrivate) {
        return new AutoValue_Repository(id, name, isPrivate);
    }

    public abstract Long id();

    public abstract String name();

    public abstract boolean isPrivate();

}
