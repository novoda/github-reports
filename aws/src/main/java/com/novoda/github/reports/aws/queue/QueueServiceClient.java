package com.novoda.github.reports.aws.queue;

public interface QueueServiceClient<Q extends Queue<M>, M extends QueueMessage> {

    Q createQueue(String name);

    void removeQueue(Q queue);

}
