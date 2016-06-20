package com.novoda.github.reports.batch.aws.credentials;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.novoda.github.reports.properties.PropertiesReader;

public class PropertiesAWSCredentialsProvider implements AWSCredentialsProvider {

    private final PropertiesReader propertiesReader;

    static PropertiesAWSCredentialsProvider newInstance(PropertiesReader propertiesReader) {
        return new PropertiesAWSCredentialsProvider(propertiesReader);
    }

    private PropertiesAWSCredentialsProvider(PropertiesReader propertiesReader) {
        this.propertiesReader = propertiesReader;
    }

    @Override
    public AWSCredentials getCredentials() {
        return PropertiesAWSCredentials.newInstance(propertiesReader);
    }

    @Override
    public void refresh() {
        // no-op
    }
}
