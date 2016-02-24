package com.novoda.reports;

import org.eclipse.egit.github.core.client.GitHubClient;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class RateLimitRetryer {

    private final GitHubClient client;
    private final Sleeper sleeper;

    public RateLimitRetryer(GitHubClient client) {
        this(client, new Sleeper());
    }

    RateLimitRetryer(GitHubClient client, Sleeper sleeper) {
        this.client = client;
        this.sleeper = sleeper;
    }

    public <T, R> void checkRateLimitAndRetry(T target, R results, int page, PaginationRetryable<T, R> retryable) {
        boolean needToRetry = checkRateLimit();
        if (needToRetry) {
            retry(target, results, page, retryable);
        }
    }

    public <T> void checkRateLimitAndRetry(T target, SingleRetryable<T> retryable) {
        boolean needToRetry = checkRateLimit();
        if (needToRetry) {
            retry(target, retryable);
        }
    }

    private boolean checkRateLimit() {
        int remainingRequests = client.getRemainingRequests();
        System.out.println("Requests left: " + remainingRequests);
        return remainingRequests == 0;
    }

    private <T, R> void retry(T target, R results, int page, PaginationRetryable<T, R> retryable) {
        sleeper.sleep();
        retryable.retry(target, results, page);
    }

    private <T> void retry(T target, SingleRetryable<T> singleRetryable) {
        sleeper.sleep();
        singleRetryable.retry(target);
    }

    public interface PaginationRetryable<T, R> {
        void retry(T target, R results, int page);
    }

    public interface SingleRetryable<T> {
        void retry(T target);
    }

    public static class Sleeper {

        private static final long ONE_HOUR = TimeUnit.HOURS.toMillis(1);

        void sleep() {
            try {
                System.err.println("Sleeping. " + LocalDateTime.now());
                Thread.sleep(ONE_HOUR);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }

    }

}
