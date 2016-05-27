package com.novoda.github.reports.batch.github.timeline;

import com.google.gson.annotations.SerializedName;
import com.novoda.github.reports.batch.github.User;

public class TimelineEvent {

    private long id;

    @SerializedName(value = "actor", alternate = {"user", "author"})
    private User actor;

    @SerializedName("event")
    private Type type;

    public long getId() {
        return id;
    }

    public User getActor() {
        return actor;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return String.format("%s [%d], by %s", type, id, actor != null ? actor.getUsername() : "{no user}");
    }

    private enum Type {

        @SerializedName("assigned")
        ASSIGNED("assigned"),
        @SerializedName("closed")
        CLOSED("closed"),
        @SerializedName("commented")
        COMMENTED("commented"),
        @SerializedName("committed")
        COMMITTED("committed"),
        @SerializedName("assigned")
        CROSS_REFERENCED("cross-referenced"),
        @SerializedName("demilestoned")
        DEMILESTONED("demilestoned"),
        @SerializedName("head_ref_deleted")
        HEAD_REF_DELETED("head_ref_deleted"),
        @SerializedName("head_ref_restored")
        HEAD_REF_RESTORED("head_ref_restored"),
        @SerializedName("labeled")
        LABELED("labeled"),
        @SerializedName("locked")
        LOCKED("locked"),
        @SerializedName("mentioned")
        MENTIONED("mentioned"),
        @SerializedName("merged")
        MERGED("merged"),
        @SerializedName("milestoned")
        MILESTONED("milestoned"),
        @SerializedName("referenced")
        REFERENCED("referenced"),
        @SerializedName("renamed")
        RENAMED("renamed"),
        @SerializedName("reopened")
        REOPENED("reopened"),
        @SerializedName("subscribed")
        SUBSCRIBED("subscribed"),
        @SerializedName("unassigned")
        UNASSIGNED("unassigned"),
        @SerializedName("unlabeled")
        UNLABELED("unlabeled"),
        @SerializedName("unlocked")
        UNLOCKED("unlocked"),
        @SerializedName("unsubscribed")
        UNSUBSCRIBED("unsubscribed");

        private final String event;

        Type(String event) {
            this.event = event;
        }

        @Override
        public String toString() {
            return event;
        }
    }
}
