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

    public RateLimitRetryer(GitHubClient client, Sleeper sleeper) {
        this.client = client;
        this.sleeper = sleeper;
    }

    public <T, R> void checkRateLimitAndRetry(T target, R results, int page, PaginationRetryable<T, R> retryable) {
        boolean needToRetry = hasHitRateLimit();
        if (needToRetry) {
            retry(target, results, page, retryable);
        }
    }

    public <T, R> void checkRateLimitAndRetry(T target, SingleRetryable<T, R> retryable) {
        boolean needToRetry = hasHitRateLimit();
        if (needToRetry) {
            retry(target, retryable);
        }
    }

    public boolean hasHitRateLimit() {
        int remainingRequests = client.getRemainingRequests();
        System.out.println("Requests left: " + remainingRequests);
        return remainingRequests == 0;
    }

    public <T, R> void retry(T target, R results, int page, PaginationRetryable<T, R> retryable) {
        sleeper.sleep();
        retryable.retry(target, results, page);
    }

    public <T, R> R retry(T target, SingleRetryable<T, R> singleRetryable) {
        sleeper.sleep();
        return singleRetryable.retry(target);
    }

    public interface PaginationRetryable<T, R> {
        void retry(T target, R results, int page);
    }

    public interface SingleRetryable<T, R> {
        R retry(T target);
    }

    public static class Sleeper {

        private static final long ONE_HOUR = TimeUnit.HOURS.toMillis(1);
        private static final long TWENTY_MINS = TimeUnit.MINUTES.toMillis(20);

        public void sleep() {
            try {
                System.out.println("Sleeping. " + LocalDateTime.now());
                Thread.sleep(ONE_HOUR + TWENTY_MINS);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            System.out.println("Resuming. " + LocalDateTime.now());
        }

    }

}
