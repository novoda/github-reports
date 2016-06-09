package com.novoda.github.reports.aws.worker;

import com.novoda.github.reports.aws.configuration.Configuration;

public interface Worker {

    void doWork(EventSource eventSource);

    void rescheduleImmediately(Configuration configuration);

    void rescheduleForLater(Configuration configuration, long minutes);

}
