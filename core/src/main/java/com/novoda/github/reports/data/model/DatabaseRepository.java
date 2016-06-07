package com.novoda.github.reports.data.model;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class DatabaseRepository {

    public static DatabaseRepository create(Long id, String name, boolean isPrivate) {
        return new AutoValue_DatabaseRepository(id, name, isPrivate);
    }

    public abstract Long id();

    public abstract String name();

    public abstract boolean isPrivate();

}
