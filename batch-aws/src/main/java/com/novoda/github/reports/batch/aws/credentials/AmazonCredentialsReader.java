package com.novoda.github.reports.batch.aws.credentials;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.novoda.github.reports.properties.PropertiesReader;

public class AmazonCredentialsReader {

    private static final String AMAZON_PROPERTIES_FILENAME = "amazon.credentials";

    private final AWSCredentialsProviderChain credentialsProviderChain;

    public static AmazonCredentialsReader newInstance() {
        PropertiesReader propertiesReader = PropertiesReader.newInstance(AMAZON_PROPERTIES_FILENAME);
        return new AmazonCredentialsReader(propertiesReader);
    }

    public static AmazonCredentialsReader newInstance(PropertiesReader propertiesReader) {
        return new AmazonCredentialsReader(propertiesReader);
    }

    private AmazonCredentialsReader(PropertiesReader propertiesReader) {
        this.credentialsProviderChain = new AWSCredentialsProviderChain(
                new EnvironmentVariableCredentialsProvider(),
                PropertiesAWSCredentialsProvider.newInstance(propertiesReader)
        );
    }

    public AWSCredentials getAWSCredentials() {
        return credentialsProviderChain.getCredentials();
    }

}
