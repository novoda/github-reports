package com.novoda.floatschedule.properties;

import com.novoda.github.reports.properties.PropertiesReader;

public class FloatCredentialsReader {

    private static final String FLOAT_PROPERTIES_FILENAME = "float.credentials";
    private static final String TOKEN_KEY = "FLOAT_OAUTH_TOKEN";

    private PropertiesReader propertiesReader;

    public static FloatCredentialsReader newInstance() {
        PropertiesReader propertiesReader = PropertiesReader.newInstance(FLOAT_PROPERTIES_FILENAME);
        return new FloatCredentialsReader(propertiesReader);
    }

    private FloatCredentialsReader(PropertiesReader propertiesReader) {
        this.propertiesReader = propertiesReader;
    }

    public String getAuthToken() {
        return propertiesReader.readProperty(TOKEN_KEY);
    }

}
