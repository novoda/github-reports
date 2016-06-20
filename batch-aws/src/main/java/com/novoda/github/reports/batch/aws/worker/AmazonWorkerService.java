package com.novoda.github.reports.batch.aws.worker;

import com.amazonaws.services.lambda.AWSLambdaClient;
import com.amazonaws.services.lambda.model.InvocationType;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.novoda.github.reports.batch.aws.configuration.AmazonConfiguration;
import com.novoda.github.reports.batch.aws.configuration.AmazonConfigurationConverter;
import com.novoda.github.reports.batch.aws.configuration.ConfigurationConverterException;
import com.novoda.github.reports.batch.aws.credentials.AmazonCredentialsReader;
import com.novoda.github.reports.batch.worker.WorkerService;
import com.novoda.github.reports.batch.worker.WorkerStartException;

public class AmazonWorkerService implements WorkerService<AmazonConfiguration> {

    private static final int SUCCESSFUL_INVOKE_RESULT_CODE = 202;

    private final LambdaPropertiesReader lambdaPropertiesReader;
    private final AWSLambdaClient awsLambdaClient;

    public static AmazonWorkerService newInstance(AmazonCredentialsReader amazonCredentialsReader, LambdaPropertiesReader lambdaPropertiesReader) {
        return new AmazonWorkerService(amazonCredentialsReader, lambdaPropertiesReader);
    }

    private AmazonWorkerService(AmazonCredentialsReader amazonCredentialsReader, LambdaPropertiesReader lambdaPropertiesReader) {
        this.lambdaPropertiesReader = lambdaPropertiesReader;
        this.awsLambdaClient = new AWSLambdaClient(amazonCredentialsReader.getAWSCredentials());
    }

    @Override
    public void startWorker(AmazonConfiguration configuration) throws WorkerStartException {
        String rawConfiguration = getConfigurationAsJson(configuration);

        InvokeRequest invokeRequest = new InvokeRequest()
                .withFunctionName(getWorkerName())
                .withPayload(rawConfiguration)
                .withInvocationType(InvocationType.Event);

        InvokeResult invokeResult = awsLambdaClient.invoke(invokeRequest);
        if (invokeResult.getStatusCode() != SUCCESSFUL_INVOKE_RESULT_CODE) {
            throw WorkerStartException.withStatusCode(invokeResult.getStatusCode());
        }
    }

    private String getConfigurationAsJson(AmazonConfiguration configuration) throws WorkerStartException {
        String rawConfiguration;
        try {
            rawConfiguration = AmazonConfigurationConverter.newInstance().toJson(configuration);
        } catch (ConfigurationConverterException e) {
            throw new WorkerStartException(e);
        }
        return rawConfiguration;
    }

    @Override
    public String getWorkerName() {
        return lambdaPropertiesReader.getLambdaName();
    }
}
