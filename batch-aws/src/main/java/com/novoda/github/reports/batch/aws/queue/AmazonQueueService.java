package com.novoda.github.reports.batch.aws.queue;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.novoda.github.reports.batch.aws.credentials.AmazonCredentialsReader;
import com.novoda.github.reports.batch.queue.QueueService;
import com.novoda.github.reports.batch.worker.Logger;

public class AmazonQueueService implements QueueService<AmazonQueue> {

    private final AmazonSQSClient amazonSQSClient;
    private final Logger logger;

    public static AmazonQueueService newInstance(AmazonCredentialsReader amazonCredentialsReader, Logger logger) {
        return new AmazonQueueService(amazonCredentialsReader, logger);
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
        logger.log("Deleting queue %s...", queueName);

        amazonSQSClient.deleteQueue(queueName);

        logger.log("Deleted queue %s.", queueName);
    }

    @Override
    public AmazonQueue getQueue(String name) {
        logger.log("Getting queue with name \"%s\"...", name);

        AmazonQueue queue = AmazonQueue.newInstance(amazonSQSClient, name, logger);

        logger.log("Got queue with name \"%s\".", name);
        return queue;
    }

}
