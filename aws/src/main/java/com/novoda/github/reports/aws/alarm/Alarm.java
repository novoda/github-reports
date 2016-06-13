package com.novoda.github.reports.aws.alarm;

import com.novoda.github.reports.aws.configuration.Configuration;
import com.novoda.github.reports.aws.configuration.NotifierConfiguration;
import com.novoda.github.reports.aws.worker.EventSource;

public interface Alarm<C extends Configuration<? extends NotifierConfiguration>> extends EventSource<C> {

    long getMinutes();

    String getName();

    String getWorkerName();

}
