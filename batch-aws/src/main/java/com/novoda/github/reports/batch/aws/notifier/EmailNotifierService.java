package com.novoda.github.reports.batch.aws.notifier;

import com.novoda.github.reports.batch.aws.configuration.AmazonConfiguration;
import com.novoda.github.reports.batch.aws.configuration.EmailNotifierConfiguration;
import com.novoda.github.reports.batch.notifier.Notifier;
import com.novoda.github.reports.batch.notifier.NotifierService;
import com.novoda.github.reports.batch.worker.Logger;

public class EmailNotifierService implements NotifierService<EmailNotifierConfiguration, AmazonConfiguration> {

    private final Logger logger;

    public static EmailNotifierService newInstance(Logger logger) {
        return new EmailNotifierService(logger);
    }

    private EmailNotifierService(Logger logger) {
        this.logger = logger;
    }

    @Override
    public Notifier<EmailNotifierConfiguration, AmazonConfiguration> getNotifier() {
        return EmailNotifier.newInstance(logger);
    }

}
