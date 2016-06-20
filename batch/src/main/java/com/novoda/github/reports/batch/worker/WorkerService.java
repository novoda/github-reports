package com.novoda.github.reports.batch.worker;

import com.novoda.github.reports.batch.configuration.Configuration;

public interface WorkerService {

    void startWorker(Configuration configuration);

    String getWorkerName();

}
