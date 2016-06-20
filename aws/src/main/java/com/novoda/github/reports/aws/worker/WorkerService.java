package com.novoda.github.reports.aws.worker;

import com.novoda.github.reports.aws.configuration.Configuration;

public interface WorkerService {

    void startWorker(Configuration configuration);

    String getWorkerName();

}
