package com.novoda.github.reports.batch.issue;

import com.google.gson.annotations.SerializedName;
import com.novoda.github.reports.batch.GithubUser;

import java.util.Date;

public class GithubEvent {

    private long id;

    private GithubUser actor;

    @SerializedName("event")
    private Type type;

    @SerializedName("created_at")
    private Date createdAt;

    Date getCreatedAt() {
        return createdAt;
    }

    public long getId() {
        return id;
    }

    GithubUser getActor() {
        return actor;
    }

    Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return String.format("%s [%d] @ %s by %s", type, id, createdAt, actor.getUsername());
    }

    public enum Type {

        @SerializedName("assigned")
        ASSIGNED("assigned"),
        @SerializedName("commented")
        COMMENTED("commented"),
        @SerializedName("closed")
        CLOSED("closed"),
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
