package com.novoda.github.reports.aws.configuration;

import com.novoda.github.reports.util.StringHelper;

import org.jetbrains.annotations.Nullable;

public interface Configuration<N extends NotifierConfiguration> {

    String jobName();

    @Nullable String alarmName();

    DatabaseConfiguration databaseConfiguration();

    GithubConfiguration githubConfiguration();

    N notifierConfiguration();

    <C extends Configuration<N>> C withAlarmName(String alarmName);

    default boolean hasAlarm() {
        return !StringHelper.isNullOrEmpty(alarmName());
    }

}
