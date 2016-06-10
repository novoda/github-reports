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

    static AmazonRawQueueMessage create(Type type,
                                        Long page,
                                        boolean isTerminal,
                                        Date since,
                                        String organisationName,
                                        String repositoryName,
                                        Long repositoryId,
                                        Long issueNumber) {

        return new AmazonRawQueueMessage(type, page, isTerminal, since, organisationName, repositoryName, repositoryId, issueNumber);
    }

    AmazonRawQueueMessage() {
        // default
    }

    private AmazonRawQueueMessage(Type type,
                                  Long page,
                                  boolean isTerminal,
                                  Date since,
                                  String organisationName,
                                  String repositoryName,
                                  Long repositoryId,
                                  Long issueNumber) {
        this.type = type;
        this.page = page;
        this.isTerminal = isTerminal;
        this.since = since;
        this.organisationName = organisationName;
        this.repositoryName = repositoryName;
        this.repositoryId = repositoryId;
        this.issueNumber = issueNumber;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AmazonRawQueueMessage that = (AmazonRawQueueMessage) o;

        if (isTerminal != that.isTerminal) {
            return false;
        }
        if (type != that.type) {
            return false;
        }
        if (organisationName != null ? !organisationName.equals(that.organisationName) : that.organisationName != null) {
            return false;
        }
        if (repositoryName != null ? !repositoryName.equals(that.repositoryName) : that.repositoryName != null) {
            return false;
        }
        if (repositoryId != null ? !repositoryId.equals(that.repositoryId) : that.repositoryId != null) {
            return false;
        }
        if (since != null ? !since.equals(that.since) : that.since != null) {
            return false;
        }
        if (issueNumber != null ? !issueNumber.equals(that.issueNumber) : that.issueNumber != null) {
            return false;
        }
        return page != null ? page.equals(that.page) : that.page == null;

    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (organisationName != null ? organisationName.hashCode() : 0);
        result = 31 * result + (repositoryName != null ? repositoryName.hashCode() : 0);
        result = 31 * result + (repositoryId != null ? repositoryId.hashCode() : 0);
        result = 31 * result + (since != null ? since.hashCode() : 0);
        result = 31 * result + (issueNumber != null ? issueNumber.hashCode() : 0);
        result = 31 * result + (page != null ? page.hashCode() : 0);
        result = 31 * result + (isTerminal ? 1 : 0);
        return result;
    }
}
