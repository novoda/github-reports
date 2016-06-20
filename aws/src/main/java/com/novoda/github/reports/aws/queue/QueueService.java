package com.novoda.github.reports.aws.queue;

public interface QueueService<Q extends Queue> {

    Q createQueue(String name);

    void removeQueue(Q queue);

    Q getQueue(String name);

}
