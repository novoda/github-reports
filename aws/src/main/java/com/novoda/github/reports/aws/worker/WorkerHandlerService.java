package com.novoda.github.reports.aws.worker;

import com.novoda.github.reports.aws.queue.QueueMessage;

public interface WorkerHandlerService<M extends QueueMessage> {

    WorkerHandler<M> getWorkerHandler();

}
