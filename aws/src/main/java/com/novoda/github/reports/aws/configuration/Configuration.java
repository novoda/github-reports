package com.novoda.github.reports.aws.configuration;

public interface Configuration<N extends NotifierConfiguration> {

    String jobName();

    DatabaseConfiguration databaseConfiguration();

    GithubConfiguration githubConfiguration();

    N notifierConfiguration();

}
