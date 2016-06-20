package com.novoda.github.reports.aws.alarm;

public interface Alarm {

    long getMinutes();

    String getName();

    String getWorkerName();

}
