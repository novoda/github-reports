package com.novoda.github.reports.aws.queue;

public interface AmazonQueueMessage extends QueueMessage {

    String receiptHandle();

}
