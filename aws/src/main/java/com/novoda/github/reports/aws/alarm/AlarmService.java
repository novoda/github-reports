package com.novoda.github.reports.aws.alarm;

import com.novoda.github.reports.aws.configuration.Configuration;

public interface AlarmService {

    Alarm createAlarm(Configuration configuration, long minutes);

    Alarm postAlarm(Alarm alarm);

    Alarm removeAlarm(Alarm alarm);

}
