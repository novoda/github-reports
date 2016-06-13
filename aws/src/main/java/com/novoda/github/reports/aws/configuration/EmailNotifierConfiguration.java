package com.novoda.github.reports.aws.configuration;

import com.google.auto.value.AutoValue;
import com.novoda.github.reports.aws.properties.EmailCredentialsReader;

@AutoValue
public abstract class EmailNotifierConfiguration implements NotifierConfiguration {

    public static EmailNotifierConfiguration create(String host, String port, boolean useSSL, String from, String username, String password) {
        return new AutoValue_EmailNotifierConfiguration(host, port, useSSL, from, username, password);
    }

    public static EmailNotifierConfiguration create(EmailCredentialsReader emailCredentialsReader) {
        return create(
                emailCredentialsReader.getHost(),
                emailCredentialsReader.getPort(),
                emailCredentialsReader.useSSL(),
                emailCredentialsReader.getFrom(),
                emailCredentialsReader.getUsername(),
                emailCredentialsReader.getPassword()
        );
    }

    abstract String host();

    abstract String port();

    abstract boolean useSSL();

    abstract String from();

    abstract String username();

    abstract String password();

}
