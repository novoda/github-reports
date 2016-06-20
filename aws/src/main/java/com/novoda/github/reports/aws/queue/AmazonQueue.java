package com.novoda.github.reports.aws.queue;

import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.PurgeQueueRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageBatchRequest;
import com.amazonaws.services.sqs.model.SendMessageBatchRequestEntry;
import com.amazonaws.services.sqs.model.SendMessageBatchResult;

import java.util.ArrayList;
import java.util.List;

public class AmazonQueue implements Queue<AmazonQueueMessage> {

    private static final Integer MAX_NUMBER_MESSAGES = 1;
    private static final Integer DEFAULT_VISIBILITY_TIMEOUT = 0;

    private final AmazonSQSClient amazonSQSClient;
    private final String queueUrl;
    private final AmazonQueueMessageConverter amazonQueueMessageConverter;

    public static AmazonQueue newInstance(AmazonSQSClient amazonSQSClient, String queueUrl) {
        AmazonQueueMessageConverter amazonQueueMessageConverter = AmazonQueueMessageConverter.newInstance();
        return new AmazonQueue(amazonQueueMessageConverter, amazonSQSClient, queueUrl);
    }

    private AmazonQueue(AmazonQueueMessageConverter amazonQueueMessageConverter, AmazonSQSClient amazonSQSClient, String queueUrl) {
        this.amazonQueueMessageConverter = amazonQueueMessageConverter;
        this.amazonSQSClient = amazonSQSClient;
        this.queueUrl = queueUrl;
    }

    @Override
    public AmazonQueueMessage getItem() throws EmptyQueueException, MessageConverterException {
        ReceiveMessageRequest receiveMessageRequest = getReceiveMessageRequest(queueUrl);
        List<Message> messages = amazonSQSClient.receiveMessage(receiveMessageRequest).getMessages();

        if (messages.isEmpty()) {
            throw new EmptyQueueException("The queue \"" + queueUrl + "\" is empty.");
        }

        Message message = messages.get(0);
        return amazonQueueMessageConverter.fromMessage(message);
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
        List<SendMessageBatchRequestEntry> sendMessageBatchRequestEntries = new ArrayList<>(size);

        for (int i = 0; i < queueMessages.size(); i++) {
            SendMessageBatchRequestEntry entry = getSendMessageRequestEntry(queueMessages.get(i), i);
            sendMessageBatchRequestEntries.add(entry);
        }

        SendMessageBatchRequest sendMessageBatchRequest = getSendMessageBatchRequest(sendMessageBatchRequestEntries);
        SendMessageBatchResult sendMessageBatchResult = amazonSQSClient.sendMessageBatch(sendMessageBatchRequest);

        if (hasBatchOperationFailed(sendMessageBatchResult)) {
            throw new QueueOperationFailedException("Add items");
        }

        return queueMessages;
    }

    private SendMessageBatchRequestEntry getSendMessageRequestEntry(AmazonQueueMessage queueMessage, int id) {
        Message message = amazonQueueMessageConverter.toMessage(queueMessage);
        return new SendMessageBatchRequestEntry()
                .withId(Integer.toString(id))
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
        DeleteMessageRequest deleteMessageRequest = new DeleteMessageRequest()
                .withQueueUrl(queueUrl)
                .withReceiptHandle(queueMessage.receiptHandle());
        amazonSQSClient.deleteMessage(deleteMessageRequest);
        return queueMessage;
    }

    @Override
    public void purgeQueue() {
        PurgeQueueRequest purgeQueueRequest = new PurgeQueueRequest().withQueueUrl(queueUrl);
        amazonSQSClient.purgeQueue(purgeQueueRequest);
    }

    public String getName() {
        return queueUrl;
    }
}
