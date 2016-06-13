package com.novoda.github.reports.batch.aws.playground;

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

public class AlarmPlayground {

    public static void main(String[] args) {
        PropertiesReader amazonPropertiesReader = PropertiesReader.newInstance("amazon.credentials");
        AmazonCredentialsService amazonCredentialsService = AmazonCredentialsService.newInstance(amazonPropertiesReader);
        AmazonAlarmService amazonAlarmService = AmazonAlarmService.newInstance(amazonCredentialsService);

        PropertiesReader databasePropertiesReader = PropertiesReader.newInstance("database.credentials");
        DatabaseCredentialsReader databaseCredentialsReader = DatabaseCredentialsReader.newInstance(databasePropertiesReader);
        DatabaseConfiguration databaseConfiguration = DatabaseConfiguration.create(databaseCredentialsReader);

        PropertiesReader githubPropertiesReader = PropertiesReader.newInstance("github.credentials");
        GithubCredentialsReader githubCredentialsReader = GithubCredentialsReader.newInstance(githubPropertiesReader);
        GithubConfiguration githubConfiguration = GithubConfiguration.create(githubCredentialsReader);

        PropertiesReader emailPropertiesReader = PropertiesReader.newInstance("email.credentials");
        EmailCredentialsReader emailCredentialsReader = EmailCredentialsReader.newInstance(emailPropertiesReader);
        EmailNotifierConfiguration notifierConfiguration = EmailNotifierConfiguration.create(emailCredentialsReader);

        AmazonConfiguration amazonConfiguration = AmazonConfiguration.create(
                "job-banana",
                databaseConfiguration,
                githubConfiguration,
                notifierConfiguration
        );

        AmazonAlarm alarm = amazonAlarmService.createAlarm(
                amazonConfiguration,
                1,
                "arn:aws:lambda:us-east-1:953109185106:function:github-reports-test-js"
        );

        amazonAlarmService.postAlarm(alarm);
    }

}
