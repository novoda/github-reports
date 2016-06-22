package com.novoda.github.reports.batch.aws.queue;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

import org.jetbrains.annotations.Nullable;

@AutoValue
public abstract class AmazonRawQueueMessage {

    static AmazonRawQueueMessage create(Type type,
                                        String organisationName,
                                        @Nullable Date since,
                                        @Nullable Boolean isTerminal,
                                        @Nullable Long page,
                                        @Nullable String repositoryName,
                                        @Nullable Long repositoryId,
                                        @Nullable Long issueNumber,
                                        @Nullable Long issueOwnerId) {

        return new AutoValue_AmazonRawQueueMessage(
                type,
                organisationName,
                since,
                isTerminal,
                page,
                repositoryName,
                repositoryId,
                issueNumber,
                issueOwnerId
        );
    }

    public static TypeAdapter<AmazonRawQueueMessage> typeAdapter(Gson gson) {
        return new AutoValue_AmazonRawQueueMessage.GsonTypeAdapter(gson);
    }

    public static Builder builder() {
        return new AutoValue_AmazonRawQueueMessage.Builder();
    }

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

    @Nullable
    abstract Long issueOwnerId();

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

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder type(Type type);

        public abstract Builder organisationName(String organisationName);

        public abstract Builder since(Date since);

        public abstract Builder isTerminal(Boolean isTerminal);

        public abstract Builder page(Long page);

        public abstract Builder repositoryName(String repositoryName);

        public abstract Builder repositoryId(Long repositoryId);

        public abstract Builder issueNumber(Long issueNumber);

        public abstract Builder issueOwnerId(Long issueOwnerId);

        public abstract AmazonRawQueueMessage build();

    }
}
