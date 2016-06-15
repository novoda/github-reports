package com.novoda.github.reports.aws.worker;

import com.novoda.github.reports.aws.configuration.Configuration;
import com.novoda.github.reports.aws.configuration.NotifierConfiguration;

public interface Worker<N extends NotifierConfiguration, C extends Configuration<N>> {

    void doWork(EventSource<N, C> eventSource) throws WorkerOperationFailedException;

    void rescheduleImmediately(C configuration);

    void rescheduleForLater(C configuration, long minutes) throws WorkerOperationFailedException;

}
