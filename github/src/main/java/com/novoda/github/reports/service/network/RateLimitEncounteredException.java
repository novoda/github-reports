package com.novoda.github.reports.service.network;

import java.util.Date;

public class RateLimitEncounteredException extends RuntimeException {

    private final Date resetDate;

    public RateLimitEncounteredException(String message, Date resetDate) {
        super(message);
        this.resetDate = resetDate;
    }

    public Date getResetDate() {
        return resetDate;
    }
}
