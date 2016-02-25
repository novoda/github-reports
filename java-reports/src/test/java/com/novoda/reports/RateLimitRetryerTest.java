package com.novoda.reports;

import org.eclipse.egit.github.core.client.GitHubClient;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class RateLimitRetryerTest {

    private MockPaginationRetryable retryable;
    private int requests;

    @Before
    public void setUp() throws Exception {
        retryable = new MockPaginationRetryable();
    }

    @Test
    public void givenNoRequestsRemaining_whenCheckingThePaginationRateLimit_theRetry() {
        RateLimitRetryer retryer = createRetryerWithRemainingRequests(0);

        retryer.checkRateLimitAndRetry(null, null, 0, retryable);

        assertThat(retryable.retryHasBeenCalled).isTrue();
    }

    @Test
    public void givenRequestsRemaining_whenCheckingThePaginationRateLimit_thenDontRetry() {
        RateLimitRetryer retryer = createRetryerWithRemainingRequests(10);

        retryer.checkRateLimitAndRetry(null, null, 0, retryable);

        assertThat(retryable.retryHasBeenCalled).isFalse();
    }

    @Test
    public void givenNoRequestsRemaining_whenCheckingTheSingleCallRateLimit_theRetry() {
        RateLimitRetryer retryer = createRetryerWithRemainingRequests(0);

        retryer.checkRateLimitAndRetry(null, retryable);

        assertThat(retryable.retryHasBeenCalled).isTrue();
    }

    @Test
    public void givenRequestsRemaining_whenCheckingTheSingleCallRateLimit_thenDontRetry() {
        RateLimitRetryer retryer = createRetryerWithRemainingRequests(10);

        retryer.checkRateLimitAndRetry(null, retryable);

        assertThat(retryable.retryHasBeenCalled).isFalse();
    }


    private RateLimitRetryer createRetryerWithRemainingRequests(int requests) {
        RateLimitRetryer retryer = new RateLimitRetryer(new StubGithubClient(), new InsomniacSleeper());
        this.requests = requests;
        return retryer;
    }

    private class StubGithubClient extends GitHubClient {

        @Override
        public int getRemainingRequests() {
            return requests;
        }
    }

    private class MockPaginationRetryable implements
            RateLimitRetryer.PaginationRetryable<Object, Object>,
            RateLimitRetryer.SingleRetryable<Object, Object> {

        boolean retryHasBeenCalled = false;

        @Override
        public void retry(Object target, Object results, int page) {
            retryHasBeenCalled = true;
        }

        @Override
        public Object retry(Object target) {
            retryHasBeenCalled = true;
            return null;
        }
    }

    private static class InsomniacSleeper extends RateLimitRetryer.Sleeper {

        @Override
        public void sleep() {
            // do nothing
        }
    }
}