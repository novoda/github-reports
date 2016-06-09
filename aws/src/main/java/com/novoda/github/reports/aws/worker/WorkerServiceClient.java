package com.novoda.github.reports.aws.worker;

import com.novoda.github.reports.aws.configuration.Configuration;

public interface WorkerServiceClient {

    void startWorker(Configuration configuration);
}
