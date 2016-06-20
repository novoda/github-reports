package com.novoda.github.reports.batch.aws.configuration;

import com.google.auto.value.AutoValue;
import com.novoda.github.reports.batch.configuration.Configuration;
import com.novoda.github.reports.batch.configuration.DatabaseConfiguration;
import com.novoda.github.reports.batch.configuration.GithubConfiguration;

import org.jetbrains.annotations.Nullable;

@AutoValue
public abstract class AmazonConfiguration implements Configuration<EmailNotifierConfiguration> {

    private static final String NO_ALARM_NAME = null;

    public static AmazonConfiguration create(String jobName,
                                             @Nullable String alarmName,
                                             DatabaseConfiguration databaseConfiguration,
                                             GithubConfiguration githubConfiguration,
                                             EmailNotifierConfiguration notifierConfiguration) {

        return builder()
                .jobName(jobName)
                .alarmName(alarmName)
                .databaseConfiguration(databaseConfiguration)
                .githubConfiguration(githubConfiguration)
                .notifierConfiguration(notifierConfiguration)
                .build();
    }

    public static Builder builder() {
        return new AutoValue_AmazonConfiguration.Builder();
    }

    public abstract EmailNotifierConfiguration notifierConfiguration();

    @Override
    public AmazonConfiguration withAlarmName(String alarmName) {
        return toBuilder().alarmName(alarmName).build();
    }

    @Override
    public AmazonConfiguration withNoAlarmName() {
        return toBuilder().alarmName(NO_ALARM_NAME).build();
    }

    abstract Builder toBuilder();

    @AutoValue.Builder
    abstract static class Builder {

        abstract Builder jobName(String jobName);

        abstract Builder alarmName(String alarmName);

        abstract Builder databaseConfiguration(DatabaseConfiguration databaseConfiguration);

        abstract Builder githubConfiguration(GithubConfiguration githubConfiguration);

        abstract Builder notifierConfiguration(EmailNotifierConfiguration notifierConfiguration);

        abstract AmazonConfiguration build();

    }
}
