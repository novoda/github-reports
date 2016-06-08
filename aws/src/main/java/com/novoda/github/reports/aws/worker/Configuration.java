package com.novoda.github.reports.aws.worker;

public interface Configuration {

    DatabaseConfiguration getDatabaseConfiguration();

    GithubConfiguration getGithubConfiguration();

    NotifierConfiguration getNotifierConfiguration();

}
