package com.novoda.github.reports.batch.alarm;

public interface Alarm {

    long getMinutes();

    String getName();

    String getWorkerName();

}
