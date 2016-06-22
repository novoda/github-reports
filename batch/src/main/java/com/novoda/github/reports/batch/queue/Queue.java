package com.novoda.github.reports.batch.queue;

import java.util.Collections;
import java.util.List;

public interface Queue<M extends QueueMessage> {

    M getItem() throws EmptyQueueException, MessageConverterException;

    List<M> addItems(List<M> queueMessages) throws QueueOperationFailedException;

    M removeItem(M queueMessage);

    void purgeQueue();

    default M addItem(M queueMessage) throws QueueOperationFailedException {
        return addItems(Collections.singletonList(queueMessage)).get(0);
    }

}
