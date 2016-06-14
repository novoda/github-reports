package com.novoda.github.reports.aws.configuration;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import java.util.List;

@AutoValue
public abstract class AmazonRawEmailNotifierConfiguration {

    public static TypeAdapter<AmazonRawEmailNotifierConfiguration> typeAdapter(Gson gson) {
        return new AutoValue_AmazonRawEmailNotifierConfiguration.GsonTypeAdapter(gson);
    }

    public static Builder builder() {
        return new AutoValue_AmazonRawEmailNotifierConfiguration.Builder();
    }

    abstract String host();

    abstract int port();

    abstract boolean useSsl();

    abstract String from();

    abstract String username();

    abstract String password();

    abstract List<String> to();

    @AutoValue.Builder
    public static abstract class Builder {

        abstract Builder host(String host);

        abstract Builder port(int port);

        abstract Builder useSsl(boolean useSsl);

        abstract Builder from(String from);

        abstract Builder username(String username);

        abstract Builder password(String password);

        abstract Builder to(List<String> to);

        abstract AmazonRawEmailNotifierConfiguration build();

    }

}
