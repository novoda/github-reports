package com.novoda.github.reports.batch.network;

class EpochTimeConverter implements TimeConverter {

    public long toMillis(long epoch) {
        return epoch * 1000L;
    }

    public long toSeconds(long millis) {
        return millis / 1000L;
    }

}
