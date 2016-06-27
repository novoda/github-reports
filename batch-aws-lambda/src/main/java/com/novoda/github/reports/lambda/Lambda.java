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
import com.novoda.github.reports.batch.logger.DefaultLogger;
import com.novoda.github.reports.batch.logger.Logger;
import com.novoda.github.reports.batch.logger.LoggerHandler;
import com.novoda.github.reports.batch.worker.BasicWorker;
import com.novoda.github.reports.batch.worker.WorkerOperationFailedException;
import com.novoda.github.reports.lambda.logger.LambdaLoggerHandler;
import com.novoda.github.reports.lambda.worker.AmazonWorkerHandlerService;

import java.io.InputStream;

public class Lambda {

    private AmazonConfigurationConverter amazonConfigurationConverter;
    private BasicWorker<AmazonAlarm, AmazonQueueMessage, AmazonQueue, EmailNotifierConfiguration, AmazonConfiguration> worker;
    private Logger lambdaLogger;

    public void handle(InputStream configuration, Context context) throws ConfigurationConverterException, WorkerOperationFailedException {
        LoggerHandler lambdaLoggerHandler = new LambdaLoggerHandler(context);
        this.lambdaLogger = DefaultLogger.newInstance(lambdaLoggerHandler);

        lambdaLogger.info("λ START.");

        init(context, lambdaLoggerHandler);

        AmazonConfiguration amazonConfiguration = amazonConfigurationConverter.fromJson(configuration);

        lambdaLogger.debug("Handling configuration:\n%s", amazonConfiguration);

        worker.doWork(amazonConfiguration);

        lambdaLogger.info("λ DONE.");
    }

    private void init(Context context, LoggerHandler loggerHandler) {
        this.amazonConfigurationConverter = AmazonConfigurationConverter.newInstance();

        AmazonCredentialsReader amazonCredentialsReader = AmazonCredentialsReader.newInstance();
        LambdaPropertiesReader lambdaPropertiesReader = LambdaPropertiesReader.newInstance();

        AmazonWorkerService workerService = AmazonWorkerService.newInstance(amazonCredentialsReader, lambdaPropertiesReader, loggerHandler);
        AmazonAlarmService alarmService = AmazonAlarmService.newInstance(amazonCredentialsReader, loggerHandler);
        AmazonQueueService queueService = AmazonQueueService.newInstance(amazonCredentialsReader, loggerHandler);
        EmailNotifierService notifierService = EmailNotifierService.newInstance(loggerHandler, context);
        AmazonWorkerHandlerService workerHandlerService = AmazonWorkerHandlerService.newInstance(loggerHandler);

        this.worker = BasicWorker.newInstance(
                workerService,
                alarmService,
                queueService,
                notifierService,
                workerHandlerService,
                loggerHandler
        );
    }

}
