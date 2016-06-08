package com.novoda.github.reports.batch.network;

public final class RateLimitRemainingResetRepositoryContainer {

    private static final RateLimitResetRepository rateLimitResetRepository = GithubRateLimitResetRepository.newInstance();

    private RateLimitRemainingResetRepositoryContainer() {
        // non-instantiable
    }

    public static RateLimitResetRepository getInstance() {
        return rateLimitResetRepository;
    }
}
