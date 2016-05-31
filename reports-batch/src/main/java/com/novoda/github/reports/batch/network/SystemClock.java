package com.novoda.github.reports.batch.network;

interface SystemClock {

    default long currentTimeMillis() {
        return System.currentTimeMillis();
    }

}
