package com.novoda.github.reports.batch.aws.notifier;

import com.novoda.github.reports.batch.aws.configuration.AmazonConfiguration;
import com.novoda.github.reports.batch.aws.configuration.EmailNotifierConfiguration;
import com.novoda.github.reports.batch.notifier.Notifier;
import com.novoda.github.reports.batch.notifier.NotifierService;

public class EmailNotifierService implements NotifierService<EmailNotifierConfiguration, AmazonConfiguration> {

    public static EmailNotifierService newInstance() {
        return new EmailNotifierService();
    }

    private EmailNotifierService() {
        // no dependencies
    }

    @Override
    public Notifier<EmailNotifierConfiguration, AmazonConfiguration> getNotifier() {
        return EmailNotifier.newInstance();
    }

}
