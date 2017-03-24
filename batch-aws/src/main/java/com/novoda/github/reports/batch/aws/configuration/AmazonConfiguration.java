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
                                             int count,
                                             DatabaseConfiguration databaseConfiguration,
                                             GithubConfiguration githubConfiguration,
                                             EmailNotifierConfiguration notifierConfiguration) {

        return builder()
                .jobName(jobName)
                .alarmName(alarmName)
                .retryCount(count)
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

    @Override
    public AmazonConfiguration withDecreasedCounter() {
        return toBuilder().retryCount(retryCount() - 1).build();
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    abstract static class Builder {

        abstract Builder jobName(String jobName);

        abstract Builder alarmName(String alarmName);

        abstract Builder databaseConfiguration(DatabaseConfiguration databaseConfiguration);

        abstract Builder githubConfiguration(GithubConfiguration githubConfiguration);

        abstract Builder notifierConfiguration(EmailNotifierConfiguration notifierConfiguration);

        abstract Builder retryCount(int count);

        abstract AmazonConfiguration build();

    }
}
