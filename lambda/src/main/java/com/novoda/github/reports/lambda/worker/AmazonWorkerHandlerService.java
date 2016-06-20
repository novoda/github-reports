package com.novoda.github.reports.lambda.worker;

import com.novoda.github.reports.aws.queue.AmazonQueueMessage;
import com.novoda.github.reports.aws.worker.WorkerHandler;
import com.novoda.github.reports.aws.worker.WorkerHandlerService;

class AmazonWorkerHandlerService implements WorkerHandlerService<AmazonQueueMessage> {

    static AmazonWorkerHandlerService newInstance() {
        return new AmazonWorkerHandlerService();
    }

    @Override
    public WorkerHandler<AmazonQueueMessage> getWorkerHandler() {
        return AmazonWorkerHandler.newInstance();
    }

}
