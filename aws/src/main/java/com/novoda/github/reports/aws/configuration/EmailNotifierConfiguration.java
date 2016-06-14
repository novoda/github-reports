package com.novoda.github.reports.aws.configuration;

import com.google.auto.value.AutoValue;
import com.novoda.github.reports.aws.properties.EmailCredentialsReader;

import java.util.List;

@AutoValue
public abstract class EmailNotifierConfiguration implements NotifierConfiguration {

    public static EmailNotifierConfiguration create(String host,
                                                    String port,
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

    abstract String host();

    abstract String port();

    abstract boolean useSsl();

    abstract String from();

    abstract String username();

    abstract String password();

    abstract List<String> to();

}
