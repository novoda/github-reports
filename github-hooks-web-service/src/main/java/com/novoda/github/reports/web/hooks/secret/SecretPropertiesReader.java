package com.novoda.github.reports.web.hooks.secret;

import com.novoda.github.reports.properties.PropertiesReader;

class SecretPropertiesReader {

    private static final String SECRET_PROPERTIES_FILENAME = "secret.properties";
    private static final String SECRET_KEY = "SECRET";

    private final PropertiesReader propertiesReader;

    public static SecretPropertiesReader newInstance() {
        PropertiesReader propertiesReader = PropertiesReader.newInstance(SECRET_PROPERTIES_FILENAME);
        return new SecretPropertiesReader(propertiesReader);
    }

    private SecretPropertiesReader(PropertiesReader propertiesReader) {
        this.propertiesReader = propertiesReader;
    }

    String getSecret() {
        return propertiesReader.readProperty(SECRET_KEY);
    }
}
