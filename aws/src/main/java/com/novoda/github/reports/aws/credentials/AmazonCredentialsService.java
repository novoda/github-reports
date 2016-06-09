package com.novoda.github.reports.aws.credentials;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.novoda.github.reports.properties.PropertiesReader;

public class AmazonCredentialsService {

    private final PropertiesReader propertiesReader;
    private final AWSCredentialsProviderChain credentialsProviderChain;

    public static AmazonCredentialsService newInstance(PropertiesReader propertiesReader) {
        return new AmazonCredentialsService(propertiesReader);
    }

    private AmazonCredentialsService(PropertiesReader propertiesReader) {
        this.propertiesReader = propertiesReader;
        this.credentialsProviderChain = new AWSCredentialsProviderChain(
                new EnvironmentVariableCredentialsProvider(),
                PropertiesAWSCredentialsProvider.newInstance(propertiesReader)
        );
    }

    public AWSCredentials getAWSCredentials() {
        return credentialsProviderChain.getCredentials();
    }

}
