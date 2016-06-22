package com.novoda.github.reports.batch.aws.worker;

import com.amazonaws.services.lambda.AWSLambdaClient;
import com.amazonaws.services.lambda.model.InvocationType;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.novoda.github.reports.batch.aws.configuration.AmazonConfiguration;
import com.novoda.github.reports.batch.aws.configuration.AmazonConfigurationConverter;
import com.novoda.github.reports.batch.aws.configuration.ConfigurationConverterException;
import com.novoda.github.reports.batch.aws.credentials.AmazonCredentialsReader;
import com.novoda.github.reports.batch.worker.Logger;
import com.novoda.github.reports.batch.worker.WorkerService;
import com.novoda.github.reports.batch.worker.WorkerStartException;

public class AmazonWorkerService implements WorkerService<AmazonConfiguration> {

    private static final int SUCCESSFUL_INVOKE_RESULT_CODE = 202;

    private final LambdaPropertiesReader lambdaPropertiesReader;
    private final AWSLambdaClient awsLambdaClient;
    private final Logger logger;
    private final AmazonConfigurationConverter amazonConfigurationConverter;

    public static AmazonWorkerService newInstance(AmazonCredentialsReader amazonCredentialsReader,
                                                  LambdaPropertiesReader lambdaPropertiesReader,
                                                  Logger logger) {

        return new AmazonWorkerService(amazonCredentialsReader, lambdaPropertiesReader, logger, AmazonConfigurationConverter.newInstance());
    }

    private AmazonWorkerService(AmazonCredentialsReader amazonCredentialsReader,
                                LambdaPropertiesReader lambdaPropertiesReader,
                                Logger logger,
                                AmazonConfigurationConverter amazonConfigurationConverter) {

        this.lambdaPropertiesReader = lambdaPropertiesReader;
        this.awsLambdaClient = new AWSLambdaClient(amazonCredentialsReader.getAWSCredentials());
        this.logger = logger;
        this.amazonConfigurationConverter = amazonConfigurationConverter;
    }

    @Override
    public void startWorker(AmazonConfiguration configuration) throws WorkerStartException {
        logger.log("Starting a new worker instance asynchronously...");

        String rawConfiguration = getConfigurationAsJson(configuration);

        InvokeRequest invokeRequest = new InvokeRequest()
                .withFunctionName(getWorkerName())
                .withPayload(rawConfiguration)
                .withInvocationType(InvocationType.Event);

        InvokeResult invokeResult = awsLambdaClient.invoke(invokeRequest);
        Integer statusCode = invokeResult.getStatusCode();
        if (statusCode != SUCCESSFUL_INVOKE_RESULT_CODE) {
            logger.log("Could not start a new worker instance, status code %d.", statusCode);
            throw WorkerStartException.withStatusCode(statusCode);
        }

        logger.log("New worker instance started asynchronously.");
    }

    private String getConfigurationAsJson(AmazonConfiguration configuration) throws WorkerStartException {
        String rawConfiguration;
        try {
            rawConfiguration = amazonConfigurationConverter.toJson(configuration);
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
