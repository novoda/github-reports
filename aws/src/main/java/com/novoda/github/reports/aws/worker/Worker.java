package com.novoda.github.reports.aws.worker;

public interface Worker {

    void doWork(EventSource eventSource);

    void reschedule(Configuration configuration, long minutes);

}
