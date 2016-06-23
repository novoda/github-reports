package com.novoda.github.reports.batch.handler;

import com.novoda.github.reports.batch.aws.credentials.AmazonCredentialsReader;
import com.novoda.github.reports.batch.aws.queue.AmazonQueue;
import com.novoda.github.reports.batch.aws.queue.AmazonQueueService;
import com.novoda.github.reports.batch.command.AwsBatchOptions;
import com.novoda.github.reports.batch.logger.DefaultLoggerHandler;

public class AwsBombCommandHandler implements CommandHandler<AwsBatchOptions> {

    private final AmazonQueueService queueService;

    public static AwsBombCommandHandler newInstance() {
        AmazonCredentialsReader amazonCredentialsReader = AmazonCredentialsReader.newInstance();
        DefaultLoggerHandler loggerHandler = new DefaultLoggerHandler();
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
    }
}
