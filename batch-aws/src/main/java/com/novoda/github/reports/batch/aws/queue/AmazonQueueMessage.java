package com.novoda.github.reports.batch.aws.queue;

import com.novoda.github.reports.batch.queue.QueueMessage;

public interface AmazonQueueMessage extends QueueMessage {

    String receiptHandle();

}
