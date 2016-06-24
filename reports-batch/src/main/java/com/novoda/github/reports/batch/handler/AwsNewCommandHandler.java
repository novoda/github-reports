package com.novoda.github.reports.batch.handler;

import com.novoda.github.reports.batch.aws.configuration.AmazonConfiguration;
import com.novoda.github.reports.batch.aws.configuration.EmailNotifierConfiguration;
import com.novoda.github.reports.batch.aws.credentials.AmazonCredentialsReader;
import com.novoda.github.reports.batch.aws.credentials.EmailCredentialsReader;
import com.novoda.github.reports.batch.aws.queue.AmazonGetRepositoriesQueueMessage;
import com.novoda.github.reports.batch.aws.queue.AmazonQueue;
import com.novoda.github.reports.batch.aws.queue.AmazonQueueMessage;
import com.novoda.github.reports.batch.aws.queue.AmazonQueueService;
import com.novoda.github.reports.batch.aws.worker.AmazonWorkerService;
import com.novoda.github.reports.batch.aws.worker.LambdaPropertiesReader;
import com.novoda.github.reports.batch.command.AwsBatchOptions;
import com.novoda.github.reports.batch.configuration.DatabaseConfiguration;
import com.novoda.github.reports.batch.configuration.GithubConfiguration;
import com.novoda.github.reports.batch.logger.DefaultLogger;
import com.novoda.github.reports.batch.logger.DefaultLoggerHandler;
import com.novoda.github.reports.data.db.properties.DatabaseCredentialsReader;
import com.novoda.github.reports.service.properties.GithubCredentialsReader;

import org.jetbrains.annotations.NotNull;

public class AwsNewCommandHandler implements CommandHandler<AwsBatchOptions> {

    private static final Boolean FIRST_TERMINAL = true;
    private static final Long FIRST_PAGE = 1L;
    private static final String RECEIPT_HANDLE = "0";
    private static final String NO_ALARM_NAME = null;
    private static DefaultLogger logger;

    private final DatabaseCredentialsReader databaseCredentialsReader;
    private final GithubCredentialsReader githubCredentialsReader;
    private final EmailCredentialsReader emailCredentialsReader;
    private final AmazonQueueService queueService;
    private final AmazonWorkerService workerService;

    public static AwsNewCommandHandler newInstance() {
        AmazonCredentialsReader amazonCredentialsReader = AmazonCredentialsReader.newInstance();
        LambdaPropertiesReader lambdaPropertiesReader = LambdaPropertiesReader.newInstance();
        DefaultLoggerHandler loggerHandler = new DefaultLoggerHandler();
        logger = DefaultLogger.newInstance(loggerHandler);

        return new AwsNewCommandHandler(
                DatabaseCredentialsReader.newInstance(),
                GithubCredentialsReader.newInstance(),
                EmailCredentialsReader.newInstance(),
                AmazonQueueService.newInstance(amazonCredentialsReader, loggerHandler),
                AmazonWorkerService.newInstance(amazonCredentialsReader, lambdaPropertiesReader, loggerHandler)
        );
    }

    private AwsNewCommandHandler(DatabaseCredentialsReader databaseCredentialsReader,
                                 GithubCredentialsReader githubCredentialsReader,
                                 EmailCredentialsReader emailCredentialsReader,
                                 AmazonQueueService queueService,
                                 AmazonWorkerService workerService) {

        this.databaseCredentialsReader = databaseCredentialsReader;
        this.githubCredentialsReader = githubCredentialsReader;
        this.emailCredentialsReader = emailCredentialsReader;
        this.queueService = queueService;
        this.workerService = workerService;
    }

    @Override
    public void handle(AwsBatchOptions options) throws Exception {
        String jobName = options.getJob();
        AmazonQueueMessage initialMessage = getInitialMessage(options);
        AmazonConfiguration initialConfiguration = getInitialConfiguration(options, jobName);

        AmazonQueue queue = queueService.createQueue(jobName);
        queue.addItem(initialMessage);
        logger.info("Queue initialised with message \"%s\" for new job \"%s\".", initialMessage.toShortString(), jobName);

        workerService.startWorker(initialConfiguration);
        logger.info("Process started on AWS Lambda.");
    }

    @NotNull
    private AmazonGetRepositoriesQueueMessage getInitialMessage(AwsBatchOptions options) {
        return AmazonGetRepositoriesQueueMessage.create(
                FIRST_TERMINAL,
                FIRST_PAGE,
                RECEIPT_HANDLE,
                options.getOrganisation(),
                options.getFrom()
        );
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
