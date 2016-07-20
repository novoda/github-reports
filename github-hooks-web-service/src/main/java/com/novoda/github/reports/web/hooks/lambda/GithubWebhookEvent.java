package com.novoda.github.reports.web.hooks.lambda;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import org.jetbrains.annotations.Nullable;

@AutoValue
public abstract class GithubWebhookEvent {

    public static GithubWebhookEvent.Builder builder() {
        return new AutoValue_GithubWebhookEvent.Builder();
    }

    public static TypeAdapter<GithubWebhookEvent> typeAdapter(Gson gson) {
        return new AutoValue_GithubWebhookEvent.GsonTypeAdapter(gson);
    }

    abstract String action();

    @Nullable
    abstract Integer number();

    @Override
    public String toString() {
        return "action="+action()+" number="+number();
    }

    @AutoValue.Builder
    public static abstract class Builder {

        abstract Builder action(String action);

        abstract Builder number(@Nullable Integer number);

        abstract GithubWebhookEvent build();
    }
}
