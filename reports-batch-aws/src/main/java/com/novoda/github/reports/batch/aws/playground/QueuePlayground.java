package com.novoda.github.reports.batch.aws.playground;

import com.novoda.github.reports.aws.credentials.AmazonCredentialsService;
import com.novoda.github.reports.aws.queue.AmazonGetIssuesQueueMessage;
import com.novoda.github.reports.aws.queue.AmazonGetRepositoriesQueueMessage;
import com.novoda.github.reports.aws.queue.AmazonQueue;
import com.novoda.github.reports.aws.queue.AmazonQueueMessage;
import com.novoda.github.reports.aws.queue.AmazonQueueService;
import com.novoda.github.reports.aws.queue.EmptyQueueException;
import com.novoda.github.reports.aws.queue.MessageConverterException;
import com.novoda.github.reports.aws.queue.QueueOperationFailedException;
import com.novoda.github.reports.properties.PropertiesReader;

import java.time.Instant;
import java.util.Arrays;

public class QueuePlayground {

    public static void main(String[] args) throws QueueOperationFailedException, EmptyQueueException, MessageConverterException {
        PropertiesReader amazonPropertiesReader = PropertiesReader.newInstance("amazon.credentials");
        AmazonCredentialsService amazonCredentialsService = AmazonCredentialsService.newInstance(amazonPropertiesReader);
        AmazonQueueService amazonQueueService = AmazonQueueService.newInstance(amazonCredentialsService);

        AmazonQueue queue = amazonQueueService.createQueue("banana-" + Instant.now().getEpochSecond());
        queue.addItems(Arrays.asList(
                AmazonGetIssuesQueueMessage.create(
                        true,
                        1L,
                        "yolo2",
                        "novoda",
                        null,
                        42L,
                        "test"
                ),
                AmazonGetRepositoriesQueueMessage.create(
                        true,
                        1L,
                        "yolo1",
                        "boh",
                        null
                )));
        AmazonQueueMessage message = queue.getItem();
        System.out.println(message);
        queue.removeItem(message);

        AmazonQueueMessage message2 = queue.getItem();
        System.out.println(message2);

        queue.purgeQueue();
        amazonQueueService.removeQueue(queue);
    }

}
