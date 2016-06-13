package com.novoda.github.reports.service.properties;

public class GithubCredentialsReader {

    private static final String GITHUB_PROPERTIES_FILENAME = "github.credentials";
    private static final String TOKEN_KEY = "GITHUB_OAUTH_TOKEN";

    private PropertiesReader propertiesReader;

    public static GithubCredentialsReader newInstance() {
        PropertiesReader propertiesReader = PropertiesReader.newInstance(GITHUB_PROPERTIES_FILENAME);
        return new GithubCredentialsReader(propertiesReader);
    }

    private GithubCredentialsReader(PropertiesReader propertiesReader) {
        this.propertiesReader = propertiesReader;
    }

    public String getAuthToken() {
        return propertiesReader.readProperty(TOKEN_KEY);
    }
}
