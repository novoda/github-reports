package com.novoda.github.reports.aws.queue;

import java.util.List;

public interface Queue {

    QueueMessage getItem();

    List<QueueMessage> addItems(List<QueueMessage> queueMessages);

    QueueMessage removeItem(QueueMessage queueMessage);

    boolean isEmpty();

    void purgeQueue();

}
