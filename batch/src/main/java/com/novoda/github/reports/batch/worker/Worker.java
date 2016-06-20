package com.novoda.github.reports.batch.worker;

import com.novoda.github.reports.batch.configuration.Configuration;
import com.novoda.github.reports.batch.configuration.NotifierConfiguration;

public interface Worker<N extends NotifierConfiguration, C extends Configuration<N>> {

    void doWork(C configuration) throws WorkerOperationFailedException;

    void rescheduleImmediately(C configuration) throws WorkerStartException;

    void rescheduleForLater(C configuration, long minutes) throws WorkerOperationFailedException;

}
