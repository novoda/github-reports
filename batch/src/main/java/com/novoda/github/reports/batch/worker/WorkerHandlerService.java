package com.novoda.github.reports.batch.worker;

import com.novoda.github.reports.batch.queue.QueueMessage;

public interface WorkerHandlerService<M extends QueueMessage> {

    WorkerHandler<M> getWorkerHandler();

}
