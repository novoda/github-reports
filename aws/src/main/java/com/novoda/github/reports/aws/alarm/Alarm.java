package com.novoda.github.reports.aws.alarm;

import com.novoda.github.reports.aws.worker.EventSource;

public interface Alarm<T> extends EventSource {

    long getMinutes();

    T getInnerAlarm();

}
