package com.novoda.github.reports.batch.alarm;

import com.novoda.github.reports.batch.configuration.Configuration;
import com.novoda.github.reports.batch.configuration.NotifierConfiguration;

public interface AlarmService<A extends Alarm, C extends Configuration<? extends NotifierConfiguration>> {

    A createNewAlarm(long minutes, String jobName, String workerDescriptor);

    A postAlarm(A alarm, C configuration) throws AlarmOperationFailedException;

    A removeAlarm(A alarm);

    String removeAlarm(String alarmName);

}
