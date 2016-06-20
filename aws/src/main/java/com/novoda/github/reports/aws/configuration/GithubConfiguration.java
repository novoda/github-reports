package com.novoda.github.reports.aws.configuration;

import com.google.auto.value.AutoValue;
import com.novoda.github.reports.service.properties.GithubCredentialsReader;

@AutoValue
public abstract class GithubConfiguration {

    public static GithubConfiguration create(String token) {
        return new AutoValue_GithubConfiguration(token);
    }

    public static GithubConfiguration create(GithubCredentialsReader githubCredentialsReader) {
        return create(githubCredentialsReader.getAuthToken());
    }

    abstract String token();

}
