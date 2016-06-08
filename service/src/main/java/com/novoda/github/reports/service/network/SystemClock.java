package com.novoda.github.reports.service.network;

interface SystemClock {

    default long currentTimeMillis() {
        return System.currentTimeMillis();
    }

}
