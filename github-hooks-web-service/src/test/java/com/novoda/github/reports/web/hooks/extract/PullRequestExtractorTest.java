package com.novoda.github.reports.web.hooks.extract;

import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.web.hooks.lambda.GithubWebhookEvent;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PullRequestExtractorTest {

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
        when(mockEvent.pullRequest()).thenReturn(mockIssue);

        GithubIssue actual = pullRequestExtractor.extractFrom(mockEvent);

        assertEquals(mockIssue, actual);
    }

    @Test(expected = ExtractException.class)
    public void givenANonPullRequestEvent_whenExtractingThePayload_thenAnExceptionIsThrown() throws Exception {
        when(mockEvent.pullRequest()).thenReturn(null);

        pullRequestExtractor.extractFrom(mockEvent);
    }

}
