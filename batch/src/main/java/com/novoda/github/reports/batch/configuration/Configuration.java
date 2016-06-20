package com.novoda.github.reports.batch.configuration;

import com.novoda.github.reports.util.StringHelper;

import org.jetbrains.annotations.Nullable;

public interface Configuration<N extends NotifierConfiguration> {

    String jobName();

    @Nullable String alarmName();

    DatabaseConfiguration databaseConfiguration();

    GithubConfiguration githubConfiguration();

    N notifierConfiguration();

    <C extends Configuration<N>> C withAlarmName(String alarmName);

    <C extends Configuration<N>> C withNoAlarmName();

    default boolean hasAlarm() {
        return !StringHelper.isNullOrEmpty(alarmName());
    }

}
