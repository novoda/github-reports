package com.novoda.github.reports.data.model;

import com.google.auto.value.AutoValue;

import java.util.Date;

@AutoValue
public abstract class DatabaseEvent {

    public static DatabaseEvent create(Long id, Long repositoryId, Long authorUserId, Long ownerUserId, EventType eventType, Date date) {
        return new AutoValue_Database_Event(id, repositoryId, authorUserId, ownerUserId, eventType, date);
    }

    public abstract Long id();

    public abstract Long repositoryId();

    public abstract Long authorUserId();

    public abstract Long ownerUserId();

    public abstract EventType eventType();

    public abstract Date date();

}
