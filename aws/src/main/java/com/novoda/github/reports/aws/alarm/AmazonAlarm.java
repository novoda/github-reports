package com.novoda.github.reports.aws.alarm;

public class AmazonAlarm implements Alarm {

    private final long minutes;
    private final String name;
    private final String workerName;

    public static AmazonAlarm newInstance(long minutes, String alarmName, String workerName) {
        return new AmazonAlarm(minutes, alarmName, workerName);
    }

    private AmazonAlarm(long minutes, String alarmName, String workerName) {
        this.minutes = minutes;
        this.name = alarmName;
        this.workerName = workerName;
    }

    @Override
    public long getMinutes() {
        return minutes;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getWorkerName() {
        return workerName;
    }
}
