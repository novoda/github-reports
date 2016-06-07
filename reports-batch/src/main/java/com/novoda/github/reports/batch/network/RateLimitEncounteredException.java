package com.novoda.github.reports.batch.network;

public class RateLimitEncounteredException extends Throwable {

    public RateLimitEncounteredException(String message) {
        super(message);
    }
}
