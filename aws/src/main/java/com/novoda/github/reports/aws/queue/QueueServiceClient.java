package com.novoda.github.reports.aws.queue;

public interface QueueServiceClient<Q extends Queue> {

    Q createQueue(String name);

    void removeQueue(Q queue);

    Q getQueue(String name);

}
