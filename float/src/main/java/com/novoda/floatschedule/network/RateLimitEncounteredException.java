package com.novoda.floatschedule.network;

public class RateLimitEncounteredException extends RuntimeException {

    RateLimitEncounteredException(String message) {
        super(message);
    }
}
