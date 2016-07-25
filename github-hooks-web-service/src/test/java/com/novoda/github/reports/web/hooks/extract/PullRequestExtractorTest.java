package com.novoda.github.reports.web.hooks.extract;

import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.MockitoAnnotations.initMocks;

public class PullRequestExtractorTest {

    private static final int ANY_ISSUE_NUMBER = 1;
    private static final int ANY_OWNER_ID = 2;

    @Mock
    private GithubWebhookEvent mockEvent;

    @InjectMocks
    private PullRequestExtractor pullRequestExtractor;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void givenAPullRequestEvent_whenExtractingThePayload_thenItIsExtracted() throws Exception {
        GithubIssue mockIssue = mock(GithubIssue.class);
        given(mockEvent.pullRequest()).willReturn(mockIssue);

        GithubIssue actual = pullRequestExtractor.extractFrom(mockEvent);

        assertEquals(mockIssue, actual);
    }

    @Test
    public void givenAPullRequestEvent_whenExtractingThePayload_thenItIsAnIssueMarkedAsAPullRequest() throws Exception {
        given(mockEvent.pullRequest()).willReturn(new GithubIssue(ANY_ISSUE_NUMBER, ANY_OWNER_ID, false));

        GithubIssue extractedIssue = pullRequestExtractor.extractFrom(mockEvent);

        assertTrue(extractedIssue.isPullRequest());
    }

    @Test(expected = ExtractException.class)
    public void givenANonPullRequestEvent_whenExtractingThePayload_thenAnExceptionIsThrown() throws Exception {
        given(mockEvent.pullRequest()).willReturn(null);

        pullRequestExtractor.extractFrom(mockEvent);
    }

}
