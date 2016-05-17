package com.novoda.github.reports.github;

interface RateLimitRemainingCounter {

    /**
     * Immediately returns the current value.
     * @return the current counter value
     */
    int get();

    /**
     * Decrements the counter and returns the new value (after decrementing).
     * @return the updated value
     */
    int decrement();

    /**
     * Immediately updates the counter to the given value.
     * @param value the value to set
     */
    void set(int value);
}
