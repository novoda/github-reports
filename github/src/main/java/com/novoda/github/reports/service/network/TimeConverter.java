package com.novoda.github.reports.service.network;

interface TimeConverter {

    long toMillis(long time);

    long toSeconds(long time);

}
