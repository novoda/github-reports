package com.novoda.github.reports.aws.alarm;

public interface AlarmServiceClient<T> {

    Alarm<T> postAlarm(Alarm<T> alarm);

    Alarm<T> removeAlarm(Alarm<T> alarm);

}
