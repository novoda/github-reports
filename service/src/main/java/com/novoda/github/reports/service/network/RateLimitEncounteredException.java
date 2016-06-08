package com.novoda.github.reports.service.network;

public class RateLimitEncounteredException extends Throwable {

    public RateLimitEncounteredException(String message) {
        super(message);
    }
}
