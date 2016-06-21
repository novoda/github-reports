package com.novoda.github.reports.lambda.worker;

import com.novoda.github.reports.batch.aws.queue.AmazonQueueMessage;
import com.novoda.github.reports.batch.worker.WorkerHandler;
import com.novoda.github.reports.batch.worker.WorkerHandlerService;

public class AmazonWorkerHandlerService implements WorkerHandlerService<AmazonQueueMessage> {

    public static AmazonWorkerHandlerService newInstance() {
        return new AmazonWorkerHandlerService();
    }

    @Override
    public WorkerHandler<AmazonQueueMessage> getWorkerHandler() {
        return new AmazonWorkerHandler();
    }

}
