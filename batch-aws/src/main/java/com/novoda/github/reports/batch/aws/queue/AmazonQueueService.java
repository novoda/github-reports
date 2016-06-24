package com.novoda.github.reports.batch.aws.queue;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.novoda.github.reports.batch.aws.credentials.AmazonCredentialsReader;
import com.novoda.github.reports.batch.logger.DefaultLogger;
import com.novoda.github.reports.batch.logger.Logger;
import com.novoda.github.reports.batch.logger.LoggerHandler;
import com.novoda.github.reports.batch.queue.QueueService;

public class AmazonQueueService implements QueueService<AmazonQueue> {

    private final AmazonSQSClient amazonSQSClient;
    private final Logger logger;

    public static AmazonQueueService newInstance(AmazonCredentialsReader amazonCredentialsReader, LoggerHandler loggerHandler) {
        return new AmazonQueueService(amazonCredentialsReader, DefaultLogger.newInstance(loggerHandler));
    }

    private AmazonQueueService(AmazonCredentialsReader amazonCredentialsReader, Logger logger) {
        AWSCredentials credentials = amazonCredentialsReader.getAWSCredentials();
        this.amazonSQSClient = new AmazonSQSClient(credentials);
        this.logger = logger;
    }

    @Override
    public AmazonQueue createQueue(String name) {
        String queueUrl = amazonSQSClient.createQueue(name).getQueueUrl();
        return AmazonQueue.newInstance(amazonSQSClient, queueUrl, logger);
    }

    @Override
    public void removeQueue(AmazonQueue queue) {
        String queueName = queue.getName();
        logger.debug("Deleting queue %s...", queueName);

        amazonSQSClient.deleteQueue(queueName);

        logger.debug("Deleted queue %s.", queueName);
    }

    @Override
    public AmazonQueue getQueue(String name) {
        logger.debug("Getting queue with name \"%s\"...", name);

        AmazonQueue queue = AmazonQueue.newInstance(amazonSQSClient, name, logger);

        logger.debug("Got queue with name \"%s\".", name);
        return queue;
    }

}
