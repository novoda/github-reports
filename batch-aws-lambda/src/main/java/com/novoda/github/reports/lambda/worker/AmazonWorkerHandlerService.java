package com.novoda.github.reports.lambda.worker;

import com.novoda.github.reports.batch.aws.queue.AmazonQueueMessage;
import com.novoda.github.reports.batch.logger.DefaultLogger;
import com.novoda.github.reports.batch.logger.Logger;
import com.novoda.github.reports.batch.logger.LoggerHandler;
import com.novoda.github.reports.batch.worker.WorkerHandler;
import com.novoda.github.reports.batch.worker.WorkerHandlerService;

public class AmazonWorkerHandlerService implements WorkerHandlerService<AmazonQueueMessage> {

    private final Logger logger;

    public static AmazonWorkerHandlerService newInstance(LoggerHandler loggerHandler) {
        return new AmazonWorkerHandlerService(DefaultLogger.newInstance(loggerHandler));
    }

    private AmazonWorkerHandlerService(Logger logger) {
        this.logger = logger;
    }

    @Override
    public WorkerHandler<AmazonQueueMessage> getWorkerHandler() {
        logger.debug("Getting worker handler...");
        WorkerHandler<AmazonQueueMessage> workerHandler = new AmazonWorkerHandler(logger);
        logger.debug("Got worker handler.");

        return workerHandler;
    }

}
