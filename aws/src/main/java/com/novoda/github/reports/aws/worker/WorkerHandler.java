package com.novoda.github.reports.aws.worker;

import com.novoda.github.reports.aws.configuration.Configuration;
import com.novoda.github.reports.aws.queue.QueueMessage;

import java.util.List;

public interface WorkerHandler<M extends QueueMessage> {

    List<M> handleQueueMessage(Configuration configuration, M queueMessage) throws Throwable;

}
