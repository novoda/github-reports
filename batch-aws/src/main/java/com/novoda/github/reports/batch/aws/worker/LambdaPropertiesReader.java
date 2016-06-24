package com.novoda.github.reports.batch.aws.worker;

import com.novoda.github.reports.properties.PropertiesReader;

public class LambdaPropertiesReader {

    private static final String LAMBDA_PROPERTIES_FILENAME = "lambda.properties";
    private static final String AWS_LAMBDA_ARN = "AWS_LAMBDA_ARN";

    private final PropertiesReader propertiesReader;

    public static LambdaPropertiesReader newInstance() {
        PropertiesReader propertiesReader = PropertiesReader.newInstance(LAMBDA_PROPERTIES_FILENAME);
        return new LambdaPropertiesReader(propertiesReader);
    }

    private LambdaPropertiesReader(PropertiesReader propertiesReader) {
        this.propertiesReader = propertiesReader;
    }

    public String getLambdaName() {
        return propertiesReader.readProperty(AWS_LAMBDA_ARN);
    }

}
