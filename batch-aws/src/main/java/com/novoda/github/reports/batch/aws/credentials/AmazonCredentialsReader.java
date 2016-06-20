package com.novoda.github.reports.batch.aws.credentials;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.novoda.github.reports.properties.PropertiesReader;

public class AmazonCredentialsReader {

    private static final String AMAZON_PROPERTIES_FILENAME = "amazon.credentials";
    private static final String AWS_LAMBDA_NAME = "AWS_LAMBDA_NAME";

    private final AWSCredentialsProviderChain credentialsProviderChain;
    private final PropertiesReader propertiesReader;

    public static AmazonCredentialsReader newInstance() {
        PropertiesReader propertiesReader = PropertiesReader.newInstance(AMAZON_PROPERTIES_FILENAME);
        return new AmazonCredentialsReader(propertiesReader);
    }

    public static AmazonCredentialsReader newInstance(PropertiesReader propertiesReader) {
        return new AmazonCredentialsReader(propertiesReader);
    }

    private AmazonCredentialsReader(PropertiesReader propertiesReader) {
        this.propertiesReader = propertiesReader;
        this.credentialsProviderChain = new AWSCredentialsProviderChain(
                new EnvironmentVariableCredentialsProvider(),
                PropertiesAWSCredentialsProvider.newInstance(propertiesReader)
        );
    }

    public AWSCredentials getAWSCredentials() {
        return credentialsProviderChain.getCredentials();
    }

    public String getLambdaName() {
        return propertiesReader.readProperty(AWS_LAMBDA_NAME);
    }

}
