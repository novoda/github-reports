package com.novoda.github.reports.web.hooks.lambda;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.novoda.github.reports.service.GithubUser;
import com.novoda.github.reports.service.issue.GithubComment;
import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.service.repository.GithubRepository;

import org.jetbrains.annotations.Nullable;

@AutoValue
public abstract class GithubWebhookEvent {

    public static GithubWebhookEvent.Builder builder() {
        return new AutoValue_GithubWebhookEvent.Builder();
    }

    public static TypeAdapter<GithubWebhookEvent> typeAdapter(Gson gson) {
        return new AutoValue_GithubWebhookEvent.GsonTypeAdapter(gson);
    }

    public abstract Action action();

    public abstract GithubUser sender();

    @Nullable
    public abstract Integer number();

    @Nullable
    //@SerializedName(value = "issue", alternate = {"pull_request"}) // not working :(
    public abstract GithubIssue issue();

    @Nullable
    @SerializedName("pull_request")
    public abstract GithubIssue pullRequest();

    @Nullable
    public abstract GithubRepository repository();

    @Nullable
    public abstract GithubComment comment();

    @Override
    public String toString() {
        return "action="+action()+" number="+number();
    }

    @AutoValue.Builder
    public static abstract class Builder {

        abstract Builder action(Action action);

        abstract Builder number(@Nullable Integer number);

        abstract Builder sender(GithubUser sender);

        abstract Builder pullRequest(@Nullable GithubIssue pullRequest);

        abstract Builder issue(@Nullable GithubIssue issue);

        abstract Builder repository(@Nullable GithubRepository repository);

        abstract Builder comment(@Nullable GithubComment comment);

        abstract GithubWebhookEvent build();
    }

    public enum Action {

        @SerializedName("opened")
        OPENED("opened"),

        @SerializedName("closed")
        CLOSED("closed"),

        @SerializedName("created")
        CREATED("created"),

        @SerializedName("added")
        ADDED("added"),

        @SerializedName("published")
        PUBLISHED("published");

        private final String action;

        Action(String action) {
            this.action = action;
        }

        @Override
        public String toString() {
            return action;
        }
    }
}
