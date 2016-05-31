package com.novoda.github.reports.batch.network;

public class TimeConverter {

    public long epochToMillis(long epoch) {
        return epoch * 1000L;
    }

    public long millisToEpoch(long millis) {
        return millis / 1000L;
    }

}
