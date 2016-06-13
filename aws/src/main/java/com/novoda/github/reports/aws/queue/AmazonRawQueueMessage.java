package com.novoda.github.reports.aws.queue;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

import org.jetbrains.annotations.Nullable;

@AutoValue
public abstract class AmazonRawQueueMessage {

    abstract Type type();

    abstract String organisationName();

    @Nullable
    abstract Date since();

    @Nullable
    abstract Boolean isTerminal();

    @Nullable
    abstract Long page();

    @Nullable
    abstract String repositoryName();

    @Nullable
    abstract Long repositoryId();

    @Nullable
    abstract Long issueNumber();

    static AmazonRawQueueMessage create(Type type,
                                        String organisationName,
                                        @Nullable Date since,
                                        @Nullable Boolean isTerminal,
                                        @Nullable Long page,
                                        @Nullable String repositoryName,
                                        @Nullable Long repositoryId,
                                        @Nullable Long issueNumber) {

        return new AutoValue_AmazonRawQueueMessage(type, organisationName, since, isTerminal, page, repositoryName, repositoryId, issueNumber);
    }

    public static TypeAdapter<AmazonRawQueueMessage> typeAdapter(Gson gson) {
        return new AutoValue_AmazonRawQueueMessage.GsonTypeAdapter(gson);
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

    public AmazonRawQueueMessage withTypeAndRepository(Type type, String repositoryName, Long repositoryId) {
        return AmazonRawQueueMessage.create(
                type,
                organisationName(),
                since(),
                isTerminal(),
                page(),
                repositoryName,
                repositoryId,
                null
        );
    }

    public AmazonRawQueueMessage withIssueNumber(Long issueNumber) {
        return AmazonRawQueueMessage.create(
                type(),
                organisationName(),
                since(),
                isTerminal(),
                page(),
                repositoryName(),
                repositoryId(),
                issueNumber
        );
    }

    public AmazonRawQueueMessage withType(Type type) {
        return AmazonRawQueueMessage.create(
                type,
                organisationName(),
                since(),
                isTerminal(),
                page(),
                repositoryName(),
                repositoryId(),
                issueNumber()
        );
    }
}
