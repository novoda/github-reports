package com.novoda.github.reports.aws.configuration;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

@AutoValue
public abstract class AmazonRawDatabaseConfiguration {

    public static TypeAdapter<AmazonRawDatabaseConfiguration> typeAdapter(Gson gson) {
        return new AutoValue_AmazonRawDatabaseConfiguration.GsonTypeAdapter(gson);
    }

    public static Builder builder() {
        return new AutoValue_AmazonRawDatabaseConfiguration.Builder();
    }

    abstract String connectionString();

    abstract String username();

    abstract String password();

    @AutoValue.Builder
    public static abstract class Builder {

        abstract Builder connectionString(String connectionString);

        abstract Builder username(String username);

        abstract Builder password(String password);

        abstract AmazonRawDatabaseConfiguration build();

    }

}
