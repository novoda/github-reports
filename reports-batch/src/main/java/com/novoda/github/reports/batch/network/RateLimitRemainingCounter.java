package com.novoda.github.reports.batch.network;

interface RateLimitRemainingCounter {

    /**
     * Immediately returns the current value.
     * @return the current counter value
     */
    int get();

    /**
     * Immediately updates the counter to the given value.
     * @param value the value to set
     */
    void set(int value);
}
