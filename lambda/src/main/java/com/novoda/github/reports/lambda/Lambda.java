package com.novoda.github.reports.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.novoda.github.reports.aws.alarm.AmazonAlarm;
import com.novoda.github.reports.aws.alarm.AmazonAlarmService;
import com.novoda.github.reports.aws.configuration.AmazonConfiguration;
import com.novoda.github.reports.aws.configuration.AmazonConfigurationConverter;
import com.novoda.github.reports.aws.configuration.ConfigurationConverterException;
import com.novoda.github.reports.aws.configuration.EmailNotifierConfiguration;
import com.novoda.github.reports.aws.credentials.AmazonCredentialsService;
import com.novoda.github.reports.aws.notifier.EmailNotifierService;
import com.novoda.github.reports.aws.queue.AmazonQueue;
import com.novoda.github.reports.aws.queue.AmazonQueueMessage;
import com.novoda.github.reports.aws.queue.AmazonQueueService;
import com.novoda.github.reports.aws.worker.AmazonWorkerService;
import com.novoda.github.reports.aws.worker.BasicWorker;
import com.novoda.github.reports.aws.worker.WorkerOperationFailedException;
import com.novoda.github.reports.lambda.worker.AmazonWorkerHandlerService;

import java.io.InputStream;

public class Lambda {

    private final AmazonConfigurationConverter amazonConfigurationConverter;
    private final BasicWorker<AmazonAlarm, AmazonQueueMessage, AmazonQueue, EmailNotifierConfiguration, AmazonConfiguration> worker;

    public Lambda() {
        this.amazonConfigurationConverter = AmazonConfigurationConverter.newInstance();

        AmazonCredentialsService amazonCredentialsService = AmazonCredentialsService.newInstance(null);
        AmazonWorkerService workerService = AmazonWorkerService.newInstance();
        AmazonAlarmService alarmService = AmazonAlarmService.newInstance(amazonCredentialsService);
        AmazonQueueService queueService = AmazonQueueService.newInstance(amazonCredentialsService);
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

        // TODO: enable this once all the pieces have been implemented
        // worker.doWork(amazonConfiguration);

        log(context, "Work done.");
    }

    private void log(Context context, String string) {
        context.getLogger().log(string);
    }

}
