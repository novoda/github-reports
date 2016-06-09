package com.novoda.github.reports.aws.configuration;

public interface Configuration {

    String getQueueName();

    DatabaseConfiguration getDatabaseConfiguration();

    GithubConfiguration getGithubConfiguration();

    NotifierConfiguration getNotifierConfiguration();

}
