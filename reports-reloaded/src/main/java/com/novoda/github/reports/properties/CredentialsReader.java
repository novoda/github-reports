package com.novoda.github.reports.properties;

import java.util.Properties;

public class CredentialsReader {

    private static final String FILENAME = "credentials.properties";
    private static final String TOKEN_KEY = "githubtoken";

    private PropertiesReader propertiesReader;

    public static CredentialsReader newInstance() {
        PropertiesReader propertiesReader = PropertiesReader.newInstance(FILENAME);
        return new CredentialsReader(propertiesReader);
    }

    CredentialsReader(PropertiesReader propertiesReader) {
        this.propertiesReader = propertiesReader;
    }

    public String getAuthToken() {
        return propertiesReader.readProperty(TOKEN_KEY);
    }
}
