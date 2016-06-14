package com.novoda.github.reports.aws.queue;

import java.util.List;

public interface Queue<M extends QueueMessage> {

    M getItem() throws EmptyQueueException, MessageConverterException;

    List<M> addItems(List<M> queueMessages) throws QueueOperationFailedException;

    M removeItem(M queueMessage);

    void purgeQueue();

}
