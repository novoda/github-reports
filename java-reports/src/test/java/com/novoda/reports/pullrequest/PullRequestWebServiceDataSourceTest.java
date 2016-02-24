package com.novoda.reports.pullrequest;

import com.novoda.reports.RateLimitRetryer;
import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.service.PullRequestService;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class PullRequestWebServiceDataSourceTest {

    private LitePullRequest litePullRequest;
    private StubPullRequestService pullRequestService;
    private MockFullConverter mockFullConverter;
    private MockRateLimitRetryer mockRateLimitRetryer;
    private PullRequestWebServiceDataSource source;

    @Before
    public void setUp() throws Exception {
        litePullRequest = new LitePullRequest("", "", 1, "", "", null);
        pullRequestService = new StubPullRequestService();
        mockFullConverter = new MockFullConverter();
        mockRateLimitRetryer = new MockRateLimitRetryer();
        source = new PullRequestWebServiceDataSource(pullRequestService, null, mockFullConverter, mockRateLimitRetryer);
    }

    @Test
    public void givenLitePullRequest_whenReadFullRequest_thenConverted() {

        source.readFullPullRequest(litePullRequest);

        assertThat(mockFullConverter.convertHasBeenCalled).isTrue();
    }

    @Test(expected = IllegalStateException.class)
    public void givenAnException_whenReadFullRequest_thenIllegalState() {
        pullRequestService.throwException = true;

        source.readFullPullRequest(litePullRequest);

        // expect exception
    }

    @Test
    public void givenAnException_whenReadFullRequest_thenCheckRateLimit() {
        pullRequestService.throwException = true;

        try {
            source.readFullPullRequest(litePullRequest);
        } catch (IllegalStateException e) {
            // ignore
        }

        assertThat(mockRateLimitRetryer.checkRateLimitAndRetryHasBeenCalled).isTrue();
    }

    private class StubPullRequestService extends PullRequestService {

        boolean throwException = false;

        @Override
        public PullRequest getPullRequest(IRepositoryIdProvider repository, int id) throws IOException {
            if (throwException) {
                throw new IOException("Test Exception");
            }
            return null;
        }
    }

    private class MockFullConverter extends FullConverter {

        boolean convertHasBeenCalled = false;

        MockFullConverter() {
            super(null);
        }

        @Override
        public FullPullRequest convert(PullRequest pullRequest) {
            convertHasBeenCalled = true;
            return null;
        }
    }

    private class MockRateLimitRetryer extends RateLimitRetryer {

        boolean checkRateLimitAndRetryHasBeenCalled = false;

        public MockRateLimitRetryer() {
            super(null);
        }

        @Override
        public <T> void checkRateLimitAndRetry(T target, SingleRetryable<T> retryable) {
            checkRateLimitAndRetryHasBeenCalled = true;
        }
    }
}