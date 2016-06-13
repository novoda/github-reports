package com.novoda.github.reports.aws.configuration;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class AmazonConfiguration implements Configuration<EmailNotifierConfiguration> {

    public static AmazonConfiguration create(String jobName,
                                             DatabaseConfiguration databaseConfiguration,
                                             GithubConfiguration githubConfiguration,
                                             EmailNotifierConfiguration notifierConfiguration) {

        return new AutoValue_AmazonConfiguration(jobName, databaseConfiguration, githubConfiguration, notifierConfiguration);
    }

}
