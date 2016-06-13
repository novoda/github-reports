package com.novoda.github.reports.aws.alarm;

import com.novoda.github.reports.aws.configuration.Configuration;
import com.novoda.github.reports.aws.configuration.NotifierConfiguration;

public interface AlarmService<A extends Alarm, C extends Configuration<? extends NotifierConfiguration>> {

    A createAlarm(C configuration, long minutes, String workerDescriptor);

    A postAlarm(A alarm);

    A removeAlarm(A alarm);

}
