package com.novoda.github.reports.batch.aws.playground;

import com.novoda.github.reports.aws.configuration.AmazonConfiguration;
import com.novoda.github.reports.aws.configuration.DatabaseConfiguration;
import com.novoda.github.reports.aws.configuration.EmailNotifierConfiguration;
import com.novoda.github.reports.aws.configuration.GithubConfiguration;
import com.novoda.github.reports.aws.notifier.EmailNotifierService;
import com.novoda.github.reports.aws.notifier.Notifier;
import com.novoda.github.reports.aws.notifier.NotifierOperationFailedException;
import com.novoda.github.reports.aws.properties.EmailCredentialsReader;
import com.novoda.github.reports.aws.queue.QueueOperationFailedException;
import com.novoda.github.reports.properties.PropertiesReader;

import java.util.Collections;
import java.util.Properties;

public class EmailNotifierPlayground {

    public static void main(String[] args) throws NotifierOperationFailedException {
        PropertiesReader emailPropertiesReader = PropertiesReader.newInstance("email.credentials");
        EmailCredentialsReader emailCredentialsReader = EmailCredentialsReader.newInstance(emailPropertiesReader);
        EmailNotifierConfiguration notifierConfiguration = EmailNotifierConfiguration.create(
                emailCredentialsReader,
                Collections.singletonList("francesco@novoda.com")
        );
        AmazonConfiguration amazonConfiguration = AmazonConfiguration.create(
                "My amazing job",
                DatabaseConfiguration.create("", new Properties()),
                GithubConfiguration.create(""),
                notifierConfiguration
        );

        EmailNotifierService emailNotifierService = EmailNotifierService.newInstance();
        Notifier<EmailNotifierConfiguration, AmazonConfiguration> notifier = emailNotifierService.getNotifier();

        notifier.notifyCompletion(amazonConfiguration);
        notifier.notifyError(amazonConfiguration, new QueueOperationFailedException("something something"));
    }

}
