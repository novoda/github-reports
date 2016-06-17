package com.novoda.github.reports.batch.aws.playground;

import com.novoda.github.reports.aws.alarm.AlarmOperationFailedException;
import com.novoda.github.reports.aws.alarm.AmazonAlarm;
import com.novoda.github.reports.aws.alarm.AmazonAlarmService;
import com.novoda.github.reports.aws.configuration.AmazonConfiguration;
import com.novoda.github.reports.aws.configuration.DatabaseConfiguration;
import com.novoda.github.reports.aws.configuration.EmailNotifierConfiguration;
import com.novoda.github.reports.aws.configuration.GithubConfiguration;
import com.novoda.github.reports.aws.credentials.AmazonCredentialsService;
import com.novoda.github.reports.aws.properties.EmailCredentialsReader;
import com.novoda.github.reports.data.db.properties.DatabaseCredentialsReader;
import com.novoda.github.reports.properties.PropertiesReader;
import com.novoda.github.reports.service.properties.GithubCredentialsReader;

import java.util.Arrays;

public class AlarmPlayground {

    public static void main(String[] args) throws AlarmOperationFailedException {
        PropertiesReader amazonPropertiesReader = PropertiesReader.newInstance("amazon.credentials");
        AmazonCredentialsService amazonCredentialsService = AmazonCredentialsService.newInstance(amazonPropertiesReader);
        AmazonAlarmService amazonAlarmService = AmazonAlarmService.newInstance(amazonCredentialsService);

        PropertiesReader databasePropertiesReader = PropertiesReader.newInstance("database.credentials");
        DatabaseCredentialsReader databaseCredentialsReader = DatabaseCredentialsReader.newInstance(databasePropertiesReader);
        DatabaseConfiguration databaseConfiguration = DatabaseConfiguration.create(databaseCredentialsReader);

        GithubCredentialsReader githubCredentialsReader = GithubCredentialsReader.newInstance();
        GithubConfiguration githubConfiguration = GithubConfiguration.create(githubCredentialsReader);

        PropertiesReader emailPropertiesReader = PropertiesReader.newInstance("email.credentials");
        EmailCredentialsReader emailCredentialsReader = EmailCredentialsReader.newInstance(emailPropertiesReader);
        EmailNotifierConfiguration notifierConfiguration = EmailNotifierConfiguration.create(
                emailCredentialsReader,
                Arrays.asList("francesco@novoda.com", "carl@novoda.com")
        );

        AmazonAlarm alarm = amazonAlarmService.createNewAlarm(
                1,
                "job-banana",
                "arn:aws:lambda:us-east-1:953109185106:function:github-reports-lambda"
        );

        AmazonConfiguration amazonConfiguration = AmazonConfiguration.create(
                "job-banana",
                alarm.getName(),
                databaseConfiguration,
                githubConfiguration,
                notifierConfiguration
        );

        amazonAlarmService.postAlarm(alarm, amazonConfiguration);
    }

}
