package com.novoda.github.reports.batch.retry;

public class RateLimitResetTimerSubjectContainer {

    private static final RateLimitResetTimerSubject SUBJECT = RateLimitResetTimerSubject.newInstance();

    private RateLimitResetTimerSubjectContainer() {
        // non-instantiable
    }

    public static RateLimitResetTimerSubject getInstance() {
        return SUBJECT;
    }

}
