package com.novoda.github.reports.github.timeline;

import com.google.gson.annotations.SerializedName;
import com.novoda.github.reports.github.User;

public class Event {

    private long id;

    private User user;

    private String body;

    private Type type;

    public enum Type {

        @SerializedName("assigned")
        ASSIGNED("assigned"),
        @SerializedName("closed")
        CLOSED("closed"),
        @SerializedName("commented")
        COMMENTED("commented"),
        @SerializedName("commited")
        COMMITED("commited"),
        @SerializedName("assigned")
        CROSS_REFERENCED("assigned"),
        @SerializedName("demilestoned")
        DEMILESTONED("demilestoned"),
        @SerializedName("assigned")
        HEAD_RED_DELETED("assigned"),
        @SerializedName("assigned")
        HEAD_REF_RESTORED("assigned"),
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
