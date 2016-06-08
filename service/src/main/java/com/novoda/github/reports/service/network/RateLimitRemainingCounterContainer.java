package com.novoda.github.reports.service.network;

public final class RateLimitRemainingCounterContainer {

    private static final RateLimitRemainingCounter remainingCounter = GithubRateLimitRemainingCounter.newInstance();

    private RateLimitRemainingCounterContainer() {
        // non-instantiable
    }

    public static RateLimitRemainingCounter getInstance() {
        return remainingCounter;
    }
}
