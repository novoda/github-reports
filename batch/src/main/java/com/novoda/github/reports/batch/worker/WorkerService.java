package com.novoda.github.reports.batch.worker;

import com.novoda.github.reports.batch.configuration.Configuration;

public interface WorkerService<C extends Configuration> {

    void startWorker(C configuration) throws WorkerStartException;

    String getWorkerName();

}
