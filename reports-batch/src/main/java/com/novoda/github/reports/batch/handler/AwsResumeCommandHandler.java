package com.novoda.github.reports.batch.handler;

import com.novoda.github.reports.batch.aws.configuration.AmazonConfiguration;
import com.novoda.github.reports.batch.aws.configuration.EmailNotifierConfiguration;
import com.novoda.github.reports.batch.aws.credentials.AmazonCredentialsReader;
import com.novoda.github.reports.batch.aws.credentials.EmailCredentialsReader;
import com.novoda.github.reports.batch.aws.worker.AmazonWorkerService;
import com.novoda.github.reports.batch.aws.worker.LambdaPropertiesReader;
import com.novoda.github.reports.batch.command.AwsBatchOptions;
import com.novoda.github.reports.batch.configuration.DatabaseConfiguration;
import com.novoda.github.reports.batch.configuration.GithubConfiguration;
import com.novoda.github.reports.batch.logger.DefaultLogger;
import com.novoda.github.reports.batch.logger.DefaultLoggerHandler;
import com.novoda.github.reports.data.db.properties.DatabaseCredentialsReader;
import com.novoda.github.reports.service.properties.GithubCredentialsReader;

public class AwsResumeCommandHandler implements CommandHandler<AwsBatchOptions> {

    private static final String NO_ALARM_NAME = null;
    private static DefaultLogger logger;

    private final DatabaseCredentialsReader databaseCredentialsReader;
    private final GithubCredentialsReader githubCredentialsReader;
    private final EmailCredentialsReader emailCredentialsReader;
    private final AmazonWorkerService workerService;

    public static AwsResumeCommandHandler newInstance() {
        AmazonCredentialsReader amazonCredentialsReader = AmazonCredentialsReader.newInstance();
        LambdaPropertiesReader lambdaPropertiesReader = LambdaPropertiesReader.newInstance();
        DefaultLoggerHandler loggerHandler = new DefaultLoggerHandler();
        logger = DefaultLogger.newInstance(loggerHandler);

        return new AwsResumeCommandHandler(
                DatabaseCredentialsReader.newInstance(),
                GithubCredentialsReader.newInstance(),
                EmailCredentialsReader.newInstance(),
                AmazonWorkerService.newInstance(amazonCredentialsReader, lambdaPropertiesReader, loggerHandler)
        );
    }

    private AwsResumeCommandHandler(DatabaseCredentialsReader databaseCredentialsReader,
                                    GithubCredentialsReader githubCredentialsReader,
                                    EmailCredentialsReader emailCredentialsReader,
                                    AmazonWorkerService workerService) {

        this.databaseCredentialsReader = databaseCredentialsReader;
        this.githubCredentialsReader = githubCredentialsReader;
        this.emailCredentialsReader = emailCredentialsReader;
        this.workerService = workerService;
    }

    @Override
    public void handle(AwsBatchOptions options) throws Exception {
        String jobName = options.getJob();
        AmazonConfiguration initialConfiguration = getInitialConfiguration(options, jobName);

        workerService.startWorker(initialConfiguration);
        logger.info("Process restarted on lamda for queue %s.", jobName);
    }

    private AmazonConfiguration getInitialConfiguration(AwsBatchOptions options, String jobName) {
        DatabaseConfiguration databaseConfiguration = DatabaseConfiguration.create(databaseCredentialsReader);
        GithubConfiguration githubConfiguration = GithubConfiguration.create(githubCredentialsReader);
        EmailNotifierConfiguration emailNotifierConfiguration = EmailNotifierConfiguration.create(emailCredentialsReader, options.getEmails());

        return AmazonConfiguration.create(
                jobName,
                NO_ALARM_NAME,
                databaseConfiguration,
                githubConfiguration,
                emailNotifierConfiguration
        );
    }
}
