package com.novoda.github.reports.batch.handler;

import com.novoda.github.reports.batch.aws.credentials.AmazonCredentialsReader;
import com.novoda.github.reports.batch.aws.queue.AmazonQueue;
import com.novoda.github.reports.batch.aws.queue.AmazonQueueService;
import com.novoda.github.reports.batch.command.AwsBatchOptions;
import com.novoda.github.reports.batch.logger.DefaultLogger;
import com.novoda.github.reports.batch.logger.DefaultLoggerHandler;

public class AwsBombCommandHandler implements CommandHandler<AwsBatchOptions> {

    private static DefaultLogger logger;
    private final AmazonQueueService queueService;

    public static AwsBombCommandHandler newInstance() {
        AmazonCredentialsReader amazonCredentialsReader = AmazonCredentialsReader.newInstance();
        DefaultLoggerHandler loggerHandler = new DefaultLoggerHandler();
        logger = DefaultLogger.newInstance(loggerHandler);
        return new AwsBombCommandHandler(AmazonQueueService.newInstance(amazonCredentialsReader, loggerHandler));
    }

    private AwsBombCommandHandler(AmazonQueueService queueService) {
        this.queueService = queueService;
    }

    @Override
    public void handle(AwsBatchOptions options) throws Exception {
        String jobName = options.getJob();
        AmazonQueue queue = queueService.getQueue(jobName);
        queue.purgeQueue();

        logger.info("Queue purged and deleted. The process will complete soon.");
    }
}
