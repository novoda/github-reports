package com.novoda.github.reports.util;

import java.time.Instant;
import java.util.Date;

public interface SystemClock {

    float SECONDS_IN_MINUTE = 60;

    static SystemClock newInstance() {
        return new SystemClock() {
        };
    }

    default long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    default long currentTimeSeconds() {
        return Instant.now().getEpochSecond();
    }

    default long getDifferenceInMinutes(Date from, Date to) {
        Instant toInstant = Instant.ofEpochMilli(to.getTime());
        long fromMillis = from.getTime();
        long diffInSeconds = toInstant.minusMillis(fromMillis).getEpochSecond();
        float diffInMinutes = diffInSeconds / SECONDS_IN_MINUTE;
        return (long) Math.ceil(Math.max(diffInMinutes, 0L));
    }

    default long getDifferenceInMinutesFromNow(Date to) {
        Date from = new Date(Instant.now().toEpochMilli());
        return getDifferenceInMinutes(from, to);
    }

}
