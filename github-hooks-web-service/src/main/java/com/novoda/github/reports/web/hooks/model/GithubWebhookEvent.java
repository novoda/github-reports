package com.novoda.github.reports.web.hooks.model;

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

    public abstract GithubAction action();

    public abstract GithubUser sender();

    @Nullable
    public abstract Integer number();

    @Nullable
    public abstract GithubIssue issue();

    @Nullable
    @SerializedName("pull_request")
    public abstract GithubWebhookPullRequest pullRequest();

    @Nullable
    public abstract GithubRepository repository();

    @Nullable
    public abstract GithubComment comment();

    @Override
    public String toString() {
        String username = sender() == null ? "[NO SENDER]" : sender().toString();
        String issueNumber = issue() == null ? "[NO ISSUE]" : issue().toString();
        String prId = pullRequest() == null ? "[NO PR]" : pullRequest().toString();
        String repoName = repository() == null ? "[NO REPO]" : repository().toString();
        String comment = comment() == null ? "[NO COMMENT]" : comment().toString();
        return String.format(
                "(%s){\naction=%s,\nuser=%s,\nissue=%s,\npr=%s,\nrepo=%s,\ncomment=%s\n}",
                getClass().getSimpleName(),
                action(),
                username,
                issueNumber,
                prId,
                repoName,
                comment
        );
    }

    @AutoValue.Builder
    public static abstract class Builder {

        public abstract Builder action(GithubAction action);

        public abstract Builder number(@Nullable Integer number);

        public abstract Builder sender(GithubUser sender);

        public abstract Builder pullRequest(@Nullable GithubWebhookPullRequest pullRequest);

        public abstract Builder issue(@Nullable GithubIssue issue);

        public abstract Builder repository(@Nullable GithubRepository repository);

        public abstract Builder comment(@Nullable GithubComment comment);

        public abstract GithubWebhookEvent build();
    }

}
