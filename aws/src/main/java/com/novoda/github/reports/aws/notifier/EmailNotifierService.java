package com.novoda.github.reports.aws.notifier;

import com.novoda.github.reports.aws.configuration.AmazonConfiguration;
import com.novoda.github.reports.aws.configuration.EmailNotifierConfiguration;

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
