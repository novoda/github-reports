package com.novoda.github.reports.batch.network;

interface TimeConverter {

    long toMillis(long time);

    long toSeconds(long time);

}
