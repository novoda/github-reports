package com.novoda.github.reports.batch.configuration;

import com.novoda.github.reports.util.StringHelper;

import org.jetbrains.annotations.Nullable;

public interface Configuration<N extends NotifierConfiguration> {

    String jobName();

    @Nullable String alarmName();

    int retryCount();

    DatabaseConfiguration databaseConfiguration();

    GithubConfiguration githubConfiguration();

    N notifierConfiguration();

    <C extends Configuration<N>> C withAlarmName(String alarmName);

    <C extends Configuration<N>> C withNoAlarmName();

    <C extends Configuration<N>> C withDecreasedCounter();

    default boolean hasAlarm() {
        return !StringHelper.isNullOrEmpty(alarmName());
    }
}
