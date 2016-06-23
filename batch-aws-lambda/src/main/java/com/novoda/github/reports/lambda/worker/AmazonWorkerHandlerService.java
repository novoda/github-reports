package com.novoda.github.reports.lambda.worker;

import com.novoda.github.reports.batch.aws.queue.AmazonQueueMessage;
import com.novoda.github.reports.batch.worker.Logger;
import com.novoda.github.reports.batch.worker.WorkerHandler;
import com.novoda.github.reports.batch.worker.WorkerHandlerService;

public class AmazonWorkerHandlerService implements WorkerHandlerService<AmazonQueueMessage> {

    private final Logger logger;

    public static AmazonWorkerHandlerService newInstance(Logger logger) {
        return new AmazonWorkerHandlerService(logger);
    }

    private AmazonWorkerHandlerService(Logger logger) {
        this.logger = logger;
    }

    @Override
    public WorkerHandler<AmazonQueueMessage> getWorkerHandler() {
        logger.log("Getting worker handler...");
        WorkerHandler<AmazonQueueMessage> workerHandler = new AmazonWorkerHandler(logger);
        logger.log("Got worker handler.");

        return workerHandler;
    }

}
