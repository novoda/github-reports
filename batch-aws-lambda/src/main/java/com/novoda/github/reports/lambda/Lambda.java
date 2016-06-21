package com.novoda.github.reports.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.novoda.github.reports.batch.aws.alarm.AmazonAlarm;
import com.novoda.github.reports.batch.aws.alarm.AmazonAlarmService;
import com.novoda.github.reports.batch.aws.configuration.AmazonConfiguration;
import com.novoda.github.reports.batch.aws.configuration.AmazonConfigurationConverter;
import com.novoda.github.reports.batch.aws.configuration.ConfigurationConverterException;
import com.novoda.github.reports.batch.aws.configuration.EmailNotifierConfiguration;
import com.novoda.github.reports.batch.aws.credentials.AmazonCredentialsReader;
import com.novoda.github.reports.batch.aws.notifier.EmailNotifierService;
import com.novoda.github.reports.batch.aws.queue.AmazonQueue;
import com.novoda.github.reports.batch.aws.queue.AmazonQueueMessage;
import com.novoda.github.reports.batch.aws.queue.AmazonQueueService;
import com.novoda.github.reports.batch.aws.worker.AmazonWorkerService;
import com.novoda.github.reports.batch.aws.worker.LambdaPropertiesReader;
import com.novoda.github.reports.batch.worker.BasicWorker;
import com.novoda.github.reports.batch.worker.WorkerOperationFailedException;
import com.novoda.github.reports.lambda.worker.AmazonWorkerHandlerService;
import com.novoda.github.reports.lambda.worker.LambdaLogger;

import java.io.InputStream;

public class Lambda {

    private AmazonConfigurationConverter amazonConfigurationConverter;
    private BasicWorker<AmazonAlarm, AmazonQueueMessage, AmazonQueue, EmailNotifierConfiguration, AmazonConfiguration> worker;
    private LambdaLogger lambdaLogger;

    public void handle(InputStream configuration, Context context) throws ConfigurationConverterException, WorkerOperationFailedException {
        init(context);

        AmazonConfiguration amazonConfiguration = amazonConfigurationConverter.fromJson(configuration);

        lambdaLogger.log("Handling configuration:");
        lambdaLogger.log(amazonConfiguration.toString());

        worker.doWork(amazonConfiguration);

        lambdaLogger.log("Work done.");
    }

    private void init(Context context) {
        this.amazonConfigurationConverter = AmazonConfigurationConverter.newInstance();
        this.lambdaLogger = new LambdaLogger(context);
        AmazonCredentialsReader amazonCredentialsReader = AmazonCredentialsReader.newInstance();
        LambdaPropertiesReader lambdaPropertiesReader = LambdaPropertiesReader.newInstance();
        AmazonWorkerService workerService = AmazonWorkerService.newInstance(amazonCredentialsReader, lambdaPropertiesReader);
        AmazonAlarmService alarmService = AmazonAlarmService.newInstance(amazonCredentialsReader);
        AmazonQueueService queueService = AmazonQueueService.newInstance(amazonCredentialsReader);
        EmailNotifierService notifierService = EmailNotifierService.newInstance();
        AmazonWorkerHandlerService workerHandlerService = AmazonWorkerHandlerService.newInstance();

        this.worker = BasicWorker.newInstance(
                workerService,
                alarmService,
                queueService,
                notifierService,
                workerHandlerService,
                lambdaLogger
        );
    }

}
