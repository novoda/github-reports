package com.novoda.github.reports.batch.aws.worker;

import com.novoda.github.reports.batch.configuration.Configuration;
import com.novoda.github.reports.batch.worker.WorkerService;

public class AmazonWorkerService implements WorkerService {

    public static AmazonWorkerService newInstance() {
        return new AmazonWorkerService();
    }

    @Override
    public void startWorker(Configuration configuration) {
        // TODO launch lambda
    }

    @Override
    public String getWorkerName() {
        // TODO read from properties
        return "github-reports";
    }
}
