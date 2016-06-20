package com.novoda.github.reports.batch.aws.configuration;

import com.google.auto.value.AutoValue;
import com.novoda.github.reports.batch.aws.credentials.EmailCredentialsReader;
import com.novoda.github.reports.batch.configuration.NotifierConfiguration;

import java.util.List;

@AutoValue
public abstract class EmailNotifierConfiguration implements NotifierConfiguration {

    public static EmailNotifierConfiguration create(String host,
                                                    int port,
                                                    boolean useSsl,
                                                    String from,
                                                    String username,
                                                    String password,
                                                    List<String> to) {

        return new AutoValue_EmailNotifierConfiguration(host, port, useSsl, from, username, password, to);
    }

    public static EmailNotifierConfiguration create(EmailCredentialsReader emailCredentialsReader, List<String> to) {
        return create(
                emailCredentialsReader.getHost(),
                emailCredentialsReader.getPort(),
                emailCredentialsReader.useSsl(),
                emailCredentialsReader.getFrom(),
                emailCredentialsReader.getUsername(),
                emailCredentialsReader.getPassword(),
                to
        );
    }

    public abstract String host();

    public abstract int port();

    public abstract boolean useSsl();

    public abstract String from();

    public abstract String username();

    public abstract String password();

    public abstract List<String> to();

}
