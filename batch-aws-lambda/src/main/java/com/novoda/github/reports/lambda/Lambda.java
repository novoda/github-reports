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
import com.novoda.github.reports.batch.worker.BasicWorker;
import com.novoda.github.reports.batch.worker.WorkerOperationFailedException;
import com.novoda.github.reports.lambda.worker.AmazonWorkerHandlerService;

import java.io.InputStream;

public class Lambda {

    private final AmazonConfigurationConverter amazonConfigurationConverter;
    private final BasicWorker<AmazonAlarm, AmazonQueueMessage, AmazonQueue, EmailNotifierConfiguration, AmazonConfiguration> worker;

    public Lambda() {
        this.amazonConfigurationConverter = AmazonConfigurationConverter.newInstance();

        AmazonCredentialsReader amazonCredentialsReader = AmazonCredentialsReader.newInstance(null);
        AmazonWorkerService workerService = AmazonWorkerService.newInstance(amazonCredentialsReader);
        AmazonAlarmService alarmService = AmazonAlarmService.newInstance(amazonCredentialsReader);
        AmazonQueueService queueService = AmazonQueueService.newInstance(amazonCredentialsReader);
        EmailNotifierService notifierService = EmailNotifierService.newInstance();
        AmazonWorkerHandlerService workerHandlerService = AmazonWorkerHandlerService.newInstance();

        this.worker = BasicWorker.newInstance(
                workerService,
                alarmService,
                queueService,
                notifierService,
                workerHandlerService
        );
    }

    public void handle(InputStream configuration, Context context) throws ConfigurationConverterException, WorkerOperationFailedException {
        AmazonConfiguration amazonConfiguration = amazonConfigurationConverter.fromJson(configuration);

        log(context, "Handling configuration:");
        log(context, amazonConfiguration.toString());

        worker.doWork(amazonConfiguration);

        log(context, "Work done.");
    }

    private void log(Context context, String string) {
        context.getLogger().log(string);
    }

}
