package com.novoda.github.reports.aws.credentials;

import com.amazonaws.auth.AWSCredentials;
import com.novoda.github.reports.properties.PropertiesReader;

class PropertiesAWSCredentials implements AWSCredentials {

    private static final String AWS_ACCESS_KEY_ID = "AWS_ACCESS_KEY_ID";
    private static final String AWS_SECRET_ACCESS_KEY = "AWS_SECRET_ACCESS_KEY";

    private final PropertiesReader propertiesReader;

    static PropertiesAWSCredentials newInstance(PropertiesReader propertiesReader) {
        return new PropertiesAWSCredentials(propertiesReader);
    }

    private PropertiesAWSCredentials(PropertiesReader propertiesReader) {
        this.propertiesReader = propertiesReader;
    }

    @Override
    public String getAWSAccessKeyId() {
        return propertiesReader.readProperty(AWS_ACCESS_KEY_ID);
    }

    @Override
    public String getAWSSecretKey() {
        return propertiesReader.readProperty(AWS_SECRET_ACCESS_KEY);
    }
}
