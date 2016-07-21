package com.novoda.github.reports.web.hooks.lambda;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.novoda.github.reports.service.GithubUser;
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

    abstract String action(); // TODO enum

    abstract GithubUser sender();

    @Nullable
    abstract Integer number();

    //@Nullable
    //@SerializedName("pull_request")
    //abstract GithubPullRequest pullRequest();

    @Nullable
    //@SerializedName(value = "issue", alternate = {"pull_request"}) // not working :(
    abstract GithubIssue issue();

    @Nullable
    @SerializedName("pull_request")
    abstract GithubIssue pullRequest();

    @Nullable
    abstract GithubRepository repository();

    @Override
    public String toString() {
        return "action="+action()+" number="+number();
    }

    @AutoValue.Builder
    public static abstract class Builder {

        abstract Builder action(String action);

        abstract Builder number(@Nullable Integer number);

        abstract Builder sender(GithubUser sender);

        //abstract Builder pullRequest(@Nullable GithubPullRequest pullRequest);
        abstract Builder pullRequest(@Nullable GithubIssue pullRequest);

        abstract Builder issue(@Nullable GithubIssue issue);

        abstract Builder repository(@Nullable GithubRepository repository);

        abstract GithubWebhookEvent build();
    }
}
