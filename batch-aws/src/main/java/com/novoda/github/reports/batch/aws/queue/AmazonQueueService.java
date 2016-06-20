package com.novoda.github.reports.batch.aws.queue;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.novoda.github.reports.batch.aws.credentials.AmazonCredentialsReader;
import com.novoda.github.reports.batch.queue.QueueService;

public class AmazonQueueService implements QueueService<AmazonQueue> {

    private final AmazonSQSClient amazonSQSClient;

    public static AmazonQueueService newInstance(AmazonCredentialsReader amazonCredentialsReader) {
        return new AmazonQueueService(amazonCredentialsReader);
    }

    private AmazonQueueService(AmazonCredentialsReader amazonCredentialsReader) {
        AWSCredentials credentials = amazonCredentialsReader.getAWSCredentials();
        this.amazonSQSClient = new AmazonSQSClient(credentials);
    }

    @Override
    public AmazonQueue createQueue(String name) {
        String queueUrl = amazonSQSClient.createQueue(name).getQueueUrl();
        return AmazonQueue.newInstance(amazonSQSClient, queueUrl);
    }

    @Override
    public void removeQueue(AmazonQueue queue) {
        amazonSQSClient.deleteQueue(queue.getName());
    }

    @Override
    public AmazonQueue getQueue(String name) {
        return AmazonQueue.newInstance(amazonSQSClient, name);
    }

}
