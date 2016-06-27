package com.novoda.github.reports.batch.aws.notifier;

import com.novoda.github.reports.batch.aws.configuration.AmazonConfiguration;
import com.novoda.github.reports.batch.aws.configuration.EmailNotifierConfiguration;
import com.novoda.github.reports.batch.logger.DefaultLogger;
import com.novoda.github.reports.batch.logger.LoggerHandler;
import com.novoda.github.reports.batch.notifier.Notifier;
import com.novoda.github.reports.batch.notifier.NotifierService;
import com.novoda.github.reports.batch.logger.Logger;

public class EmailNotifierService implements NotifierService<EmailNotifierConfiguration, AmazonConfiguration> {

    private final Logger logger;
    private final AdditionalInfo additionalInfo;

    public static EmailNotifierService newInstance(LoggerHandler loggerHandler, AdditionalInfo additionalInfo) {
        return new EmailNotifierService(DefaultLogger.newInstance(loggerHandler), additionalInfo);
    }

    private EmailNotifierService(Logger logger, AdditionalInfo additionalInfo) {
        this.logger = logger;
        this.additionalInfo = additionalInfo;
    }

    @Override
    public Notifier<EmailNotifierConfiguration, AmazonConfiguration> getNotifier() {
        return EmailNotifier.newInstance(logger, additionalInfo);
    }

}
