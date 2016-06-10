package com.novoda.github.reports.aws.queue;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class AmazonRawQueueMessage {

    public Type type;

    public String organisationName;

    public String repositoryName;

    public Long repositoryId;

    public Date since;

    public Long issueNumber;

    public Long page;

    public boolean isTerminal;

    public Type getType() {
        return type;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public Long getRepositoryId() {
        return repositoryId;
    }

    public Date getSince() {
        return since;
    }

    public Long getIssueNumber() {
        return issueNumber;
    }

    public Long getPage() {
        return page;
    }

    public boolean isTerminal() {
        return isTerminal;
    }

    public enum Type {
        @SerializedName("repositories")
        REPOSITORIES("repositories"),

        @SerializedName("issues")
        ISSUES("issues"),

        @SerializedName("comments")
        COMMENTS("comments"),

        @SerializedName("events")
        EVENTS("events"),

        @SerializedName("review_comments")
        REVIEW_COMMENTS("review_comments");

        private final String type;

        Type(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return type;
        }
    }

}
