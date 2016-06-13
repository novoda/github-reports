package com.novoda.github.reports.aws.worker;

import com.novoda.github.reports.aws.configuration.Configuration;
import com.novoda.github.reports.aws.configuration.NotifierConfiguration;

public interface Worker<C extends Configuration<NotifierConfiguration>> {

    void doWork(EventSource<C> eventSource);

    void rescheduleImmediately(C configuration);

    void rescheduleForLater(C configuration, long minutes);

}
