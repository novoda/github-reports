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
import com.novoda.github.reports.batch.worker.Logger;
import com.novoda.github.reports.batch.worker.WorkerOperationFailedException;
import com.novoda.github.reports.lambda.worker.AmazonWorkerHandlerService;
import com.novoda.github.reports.lambda.worker.LambdaLogger;

import java.io.InputStream;

public class Lambda {

    private AmazonConfigurationConverter amazonConfigurationConverter;
    private BasicWorker<AmazonAlarm, AmazonQueueMessage, AmazonQueue, EmailNotifierConfiguration, AmazonConfiguration> worker;
    private Logger logger;

    public void handle(InputStream configuration, Context context) throws ConfigurationConverterException, WorkerOperationFailedException {
        init(context);

        AmazonConfiguration amazonConfiguration = amazonConfigurationConverter.fromJson(configuration);

        logger.log("Handling configuration:\n" + amazonConfiguration);

        worker.doWork(amazonConfiguration);

        logger.log("Work done.");
    }

    private void init(Context context) {
        this.amazonConfigurationConverter = AmazonConfigurationConverter.newInstance();
        this.logger = new LambdaLogger(context);

        AmazonCredentialsReader amazonCredentialsReader = AmazonCredentialsReader.newInstance();
        LambdaPropertiesReader lambdaPropertiesReader = LambdaPropertiesReader.newInstance();

        AmazonWorkerService workerService = AmazonWorkerService.newInstance(amazonCredentialsReader, lambdaPropertiesReader, logger);
        AmazonAlarmService alarmService = AmazonAlarmService.newInstance(amazonCredentialsReader, logger);
        AmazonQueueService queueService = AmazonQueueService.newInstance(amazonCredentialsReader, logger);
        EmailNotifierService notifierService = EmailNotifierService.newInstance(logger);
        AmazonWorkerHandlerService workerHandlerService = AmazonWorkerHandlerService.newInstance(logger);

        this.worker = BasicWorker.newInstance(
                workerService,
                alarmService,
                queueService,
                notifierService,
                workerHandlerService,
                logger
        );
    }

}
