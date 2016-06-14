package com.novoda.github.reports.aws.alarm;

import com.novoda.github.reports.aws.configuration.AmazonConfiguration;

public class AmazonAlarm implements Alarm<AmazonConfiguration> {

    private final AmazonConfiguration configuration;
    private final long minutes;
    private final String name;
    private final String workerName;

    static AmazonAlarm newInstance(AmazonConfiguration configuration, long minutes, String alarmName, String workerName) {
        return new AmazonAlarm(configuration, minutes, alarmName, workerName);
    }

    private AmazonAlarm(AmazonConfiguration configuration, long minutes, String alarmName, String workerName) {
        this.configuration = configuration;
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

    @Override
    public AmazonConfiguration getConfiguration() {
        return configuration;
    }
}
