package com.novoda.github.reports.batch.aws.configuration;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

@AutoValue
public abstract class AmazonRawGithubConfiguration {

    public static TypeAdapter<AmazonRawGithubConfiguration> typeAdapter(Gson gson) {
        return new AutoValue_AmazonRawGithubConfiguration.GsonTypeAdapter(gson);
    }

    public static Builder builder() {
        return new AutoValue_AmazonRawGithubConfiguration.Builder();
    }

    abstract String token();

    @AutoValue.Builder
    public static abstract class Builder {

        abstract Builder token(String token);

        abstract AmazonRawGithubConfiguration build();

    }

}
