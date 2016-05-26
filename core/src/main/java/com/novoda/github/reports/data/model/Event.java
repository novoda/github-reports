package com.novoda.github.reports.data.model;

import com.google.auto.value.AutoValue;

import java.util.Date;

@AutoValue
public abstract class Event {

    public static Event create(Integer id, Integer repositoryId, Integer authorUserId, Integer ownerUserId, EventType eventType, Date date) {
        return new AutoValue_Event(id, repositoryId, authorUserId, ownerUserId, eventType, date);
    }

    public abstract Integer id();

    public abstract Integer repositoryId();

    public abstract Integer authorUserId();

    public abstract Integer ownerUserId();

    public abstract EventType eventType();

    public abstract Date date();

}
