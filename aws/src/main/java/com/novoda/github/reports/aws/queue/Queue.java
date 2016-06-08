package com.novoda.github.reports.aws.queue;

public interface Queue<T extends QueueMessage> {

    T getItem();

    T removeItem();

    boolean isEmpty();

    void purgeQueue();

}
