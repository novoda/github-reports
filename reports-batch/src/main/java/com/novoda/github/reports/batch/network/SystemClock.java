package com.novoda.github.reports.batch.network;

public interface SystemClock {

    default long currentTimeMillis() {
        return System.currentTimeMillis();
    }

}
