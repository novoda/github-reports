package com.novoda.reports.pullrequest.comment;

import com.novoda.reports.RateLimitRetryer;
import com.novoda.reports.pullrequest.LitePullRequest;
import org.eclipse.egit.github.core.CommitComment;
import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.NoSuchPageException;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.client.PagedRequest;
import org.eclipse.egit.github.core.service.PullRequestService;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Stack;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentWebServiceDataSourceTest {

    private CommentWebServiceDataSource dataSource;
    private MockPullRequestService service;
    private MockSleeper sleeper;

    @Before
    public void setUp() throws Exception {
        service = new MockPullRequestService();
        sleeper = new MockSleeper();
        RateLimitRetryer retryer = new RateLimitRetryer(new MockGitHubClient(), sleeper);
        CommentConverter converter = new CommentConverter();
        dataSource = new CommentWebServiceDataSource(service, converter, retryer);
    }

    @Test
    public void givenWeHitTheRateLimit_whenWeReadComments_thenSleepIsCalled() throws Exception {
        LitePullRequest lpr = new LitePullRequest("testRepo", "testLogin", 99, "title", "userLogin", LocalDate.now());
        service.addPages(3);

        dataSource.readComments(lpr);

        assertThat(sleeper.totalSleepCalls).isEqualTo(3);
    }

    private class MockPullRequestService extends PullRequestService {

        Stack<Boolean> hasPagesResult = new Stack<>();

        public void addPages(int total) {
            hasPagesResult.add(false);
            for (int i = 0; i < total; i++) {
                hasPagesResult.add(true);
            }
        }

        @Override
        public PageIterator<CommitComment> pageComments(IRepositoryIdProvider repository, int id, int start, int size) {
            PagedRequest<CommitComment> pagedRequest = new PagedRequest<>();
            return new PageIterator<CommitComment>(pagedRequest, null) {
                @Override
                public boolean hasNext() {
                    return hasPagesResult.pop();
                }

                @Override
                public Collection<CommitComment> next() {
                    throw new NoSuchPageException(new IOException("test exception"));
                }
            };
        }

        @Override
        public GitHubClient getClient() {
            return new MockGitHubClient();
        }
    }

    private class MockSleeper extends RateLimitRetryer.Sleeper {

        int totalSleepCalls = 0;

        @Override
        public void sleep() {
            // don't sleep at all
            totalSleepCalls++;
        }
    }

    private class MockGitHubClient extends GitHubClient {

        @Override
        public int getRemainingRequests() {
            return 0;
        }
    }
}