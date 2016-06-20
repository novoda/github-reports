package com.novoda.github.reports.batch.aws.credentials;

import com.novoda.github.reports.properties.PropertiesReader;

public class EmailCredentialsReader {

    private static final String HOST_KEY = "EMAIL_HOST";
    private static final String PORT_KEY = "EMAIL_PORT";
    private static final String USE_SSL_KEY = "EMAIL_USE_SSL";
    private static final String FROM_KEY = "EMAIL_FROM";
    private static final String USERNAME_KEY = "EMAIL_USERNAME";
    private static final String PASSWORD_KEY = "EMAIL_PASSWORD";

    private PropertiesReader propertiesReader;

    public static EmailCredentialsReader newInstance(PropertiesReader propertiesReader) {
        return new EmailCredentialsReader(propertiesReader);
    }

    private EmailCredentialsReader(PropertiesReader propertiesReader) {
        this.propertiesReader = propertiesReader;
    }

    public String getHost() {
        return propertiesReader.readProperty(HOST_KEY);
    }

    public int getPort() {
        return propertiesReader.readPropertyAsInt(PORT_KEY);
    }

    public boolean useSsl() {
        return propertiesReader.readPropertyAsBoolean(USE_SSL_KEY);
    }

    public String getFrom() {
        return propertiesReader.readProperty(FROM_KEY);
    }

    public String getUsername() {
        return propertiesReader.readProperty(USERNAME_KEY);
    }

    public String getPassword() {
        return propertiesReader.readProperty(PASSWORD_KEY);
    }

}
