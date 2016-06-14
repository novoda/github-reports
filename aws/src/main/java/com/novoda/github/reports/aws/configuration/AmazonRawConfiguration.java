package com.novoda.github.reports.aws.configuration;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

@AutoValue
public abstract class AmazonRawConfiguration {

    public static TypeAdapter<AmazonRawConfiguration> typeAdapter(Gson gson) {
        return new AutoValue_AmazonRawConfiguration.GsonTypeAdapter(gson);
    }

    public static Builder builder() {
        return new AutoValue_AmazonRawConfiguration.Builder();
    }

    abstract String jobName();

    abstract AmazonRawDatabaseConfiguration database();

    abstract AmazonRawGithubConfiguration github();

    abstract AmazonRawEmailNotifierConfiguration email();

    @AutoValue.Builder
    public static abstract class Builder {

        abstract Builder jobName(String jobName);

        abstract Builder database(AmazonRawDatabaseConfiguration database);

        abstract Builder github(AmazonRawGithubConfiguration github);

        abstract Builder email(AmazonRawEmailNotifierConfiguration email);

        abstract AmazonRawConfiguration build();

    }

}
