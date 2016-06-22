package com.novoda.github.reports.batch.aws.queue;

import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.PurgeQueueRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageBatchRequest;
import com.amazonaws.services.sqs.model.SendMessageBatchRequestEntry;
import com.amazonaws.services.sqs.model.SendMessageBatchResult;
import com.novoda.github.reports.batch.queue.EmptyQueueException;
import com.novoda.github.reports.batch.queue.MessageConverterException;
import com.novoda.github.reports.batch.queue.Queue;
import com.novoda.github.reports.batch.queue.QueueOperationFailedException;
import com.novoda.github.reports.batch.worker.Logger;

import java.util.ArrayList;
import java.util.List;

public class AmazonQueue implements Queue<AmazonQueueMessage> {

    private static final Integer MAX_NUMBER_MESSAGES = 1;
    private static final Integer DEFAULT_VISIBILITY_TIMEOUT = 0;
    private static final float BATCH_MAX_SIZE = 10f;

    private final AmazonSQSClient amazonSQSClient;
    private final String queueUrl;
    private final AmazonQueueMessageConverter amazonQueueMessageConverter;
    private final Logger logger;

    public static AmazonQueue newInstance(AmazonSQSClient amazonSQSClient, String queueUrl, Logger logger) {
        AmazonQueueMessageConverter amazonQueueMessageConverter = AmazonQueueMessageConverter.newInstance();
        return new AmazonQueue(amazonQueueMessageConverter, amazonSQSClient, queueUrl, logger);
    }

    private AmazonQueue(AmazonQueueMessageConverter amazonQueueMessageConverter, AmazonSQSClient amazonSQSClient, String queueUrl, Logger logger) {
        this.amazonQueueMessageConverter = amazonQueueMessageConverter;
        this.amazonSQSClient = amazonSQSClient;
        this.queueUrl = queueUrl;
        this.logger = logger;
    }

    @Override
    public AmazonQueueMessage getItem() throws EmptyQueueException, MessageConverterException {
        logger.log("Getting first item from queue...");

        ReceiveMessageRequest receiveMessageRequest = getReceiveMessageRequest(queueUrl);
        List<Message> messages = amazonSQSClient.receiveMessage(receiveMessageRequest).getMessages();

        if (messages.isEmpty()) {
            throw new EmptyQueueException("The queue \"" + queueUrl + "\" is empty.");
        }

        Message message = messages.get(0);
        AmazonQueueMessage item = amazonQueueMessageConverter.fromMessage(message);

        logger.log("Got item:\n%s", item);

        return item;
    }

    private ReceiveMessageRequest getReceiveMessageRequest(String queueUrl) {
        return new ReceiveMessageRequest()
                .withQueueUrl(queueUrl)
                .withMaxNumberOfMessages(MAX_NUMBER_MESSAGES)
                .withVisibilityTimeout(DEFAULT_VISIBILITY_TIMEOUT);
    }

    @Override
    public List<AmazonQueueMessage> addItems(List<AmazonQueueMessage> queueMessages) throws QueueOperationFailedException {
        int size = queueMessages.size();
        logger.log("Adding %d new items in the queue...", size);

        List<List<SendMessageBatchRequestEntry>> queueMessageBatches = bufferQueueMessagesAsBatches(queueMessages);

        for (List<SendMessageBatchRequestEntry> queueMessageBatch : queueMessageBatches) {
            sendQueueMessageBatch(queueMessageBatch);
        }

        logger.log("Added %d new items in the queue.", size);

        return queueMessages;
    }

    private void sendQueueMessageBatch(List<SendMessageBatchRequestEntry> queueMessageBatch) throws QueueOperationFailedException {
        SendMessageBatchRequest sendMessageBatchRequest = getSendMessageBatchRequest(queueMessageBatch);
        SendMessageBatchResult sendMessageBatchResult = amazonSQSClient.sendMessageBatch(sendMessageBatchRequest);

        if (hasBatchOperationFailed(sendMessageBatchResult)) {
            throw new QueueOperationFailedException("Add items");
        }
    }

    private List<List<SendMessageBatchRequestEntry>> bufferQueueMessagesAsBatches(List<AmazonQueueMessage> queueMessages) {
        int originalSize = queueMessages.size();
        int batches = calculateNumberOfAddItemsBatches(originalSize);

        List<List<SendMessageBatchRequestEntry>> queueMessageBatches = new ArrayList<>(batches);

        for (int batchCounter = 0; batchCounter < batches; batchCounter++) {
            List<SendMessageBatchRequestEntry> sendMessageBatchRequestEntries = new ArrayList<>();
            int counter = (int) (batchCounter * BATCH_MAX_SIZE);
            int finalCounter = (int) Math.min(counter + BATCH_MAX_SIZE, originalSize);

            for (; counter < finalCounter; counter++) {
                AmazonQueueMessage queueMessage = queueMessages.get(counter);
                SendMessageBatchRequestEntry sendMessageRequestEntry = getSendMessageRequestEntry(queueMessage, counter);
                sendMessageBatchRequestEntries.add(sendMessageRequestEntry);
            }

            queueMessageBatches.add(sendMessageBatchRequestEntries);
        }
        return queueMessageBatches;
    }

    private int calculateNumberOfAddItemsBatches(int originalSize) {
        return (int) Math.ceil(originalSize / BATCH_MAX_SIZE);
    }

    private SendMessageBatchRequestEntry getSendMessageRequestEntry(AmazonQueueMessage queueMessage, int id) {
        Message message = amazonQueueMessageConverter.toMessage(queueMessage);
        return new SendMessageBatchRequestEntry()
                .withId(Integer.toString(id))
                //.withMessageAttributes(DEFAULT_MESSAGE_ATTRIBUTES)
                .withMessageBody(message.getBody());
    }

    private SendMessageBatchRequest getSendMessageBatchRequest(List<SendMessageBatchRequestEntry> entries) {
        return new SendMessageBatchRequest()
                .withQueueUrl(queueUrl)
                .withEntries(entries);
    }

    private boolean hasBatchOperationFailed(SendMessageBatchResult sendMessageBatchResult) {
        return !sendMessageBatchResult.getFailed().isEmpty();
    }

    @Override
    public AmazonQueueMessage removeItem(AmazonQueueMessage queueMessage) {
        logger.log("Removing item from the queue...");

        DeleteMessageRequest deleteMessageRequest = new DeleteMessageRequest()
                .withQueueUrl(queueUrl)
                .withReceiptHandle(queueMessage.receiptHandle());
        amazonSQSClient.deleteMessage(deleteMessageRequest);

        logger.log("Removed item:\n%s", queueMessage);

        return queueMessage;
    }

    @Override
    public void purgeQueue() {
        logger.log("Purging the queue...");
        PurgeQueueRequest purgeQueueRequest = new PurgeQueueRequest().withQueueUrl(queueUrl);
        amazonSQSClient.purgeQueue(purgeQueueRequest);
        logger.log("Queue purged.");
    }

    String getName() {
        return queueUrl;
    }
}
