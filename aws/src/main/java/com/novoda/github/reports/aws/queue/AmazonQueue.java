package com.novoda.github.reports.aws.queue;

import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.PurgeQueueRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageBatchRequest;
import com.amazonaws.services.sqs.model.SendMessageBatchRequestEntry;
import com.amazonaws.services.sqs.model.SendMessageBatchResult;

import java.util.List;
import java.util.stream.Collectors;

public class AmazonQueue implements Queue<AmazonQueueMessage> {

    private static final Integer MAX_NUMBER_MESSAGES = 0;

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
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest()
                .withQueueUrl(queueUrl)
                .withMaxNumberOfMessages(MAX_NUMBER_MESSAGES);
        List<Message> messages = amazonSQSClient.receiveMessage(receiveMessageRequest).getMessages();
        if (messages.isEmpty()) {
            throw new EmptyQueueException("The queue \"" + queueUrl + "\" is empty.");
        }

        Message message = messages.get(0);
        return amazonQueueMessageConverter.fromMessage(message);
    }

    @Override
    public List<AmazonQueueMessage> addItems(List<AmazonQueueMessage> queueMessages) throws QueueOperationFailedException {
        List<SendMessageBatchRequestEntry> sendMessageBatchRequestEntries = queueMessages.stream()
                .map(this::getSendMessageRequestEntry)
                .collect(Collectors.toList());
        SendMessageBatchRequest sendMessageBatchRequest = new SendMessageBatchRequest()
                .withQueueUrl(queueUrl)
                .withEntries(sendMessageBatchRequestEntries);

        SendMessageBatchResult sendMessageBatchResult = amazonSQSClient.sendMessageBatch(sendMessageBatchRequest);
        if (!sendMessageBatchResult.getFailed().isEmpty()) {
            throw new QueueOperationFailedException("Add items");
        }

        return queueMessages;
    }

    private SendMessageBatchRequestEntry getSendMessageRequestEntry(AmazonQueueMessage queueMessage) {
        Message message = amazonQueueMessageConverter.toMessage(queueMessage);
        return new SendMessageBatchRequestEntry()
                .withMessageBody(message.getBody());
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
