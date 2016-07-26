package com.novoda.github.reports.web.hooks.extract;

import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.initMocks;

public class IssueExtractorTest {

    private static final int ANY_ISSUE_NUMBER = 23;
    private static final long ANY_OWNER_ID = 88;
    private static final boolean ANY_IS_PULL_REQUEST = false;

    @Mock
    private GithubWebhookEvent mockEvent;

    @InjectMocks
    private IssueExtractor extractor;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void givenAnIssueEvent_whenExtractingThePayload_thenItIsExtracted() throws Exception {
        GithubIssue issue = new GithubIssue(ANY_ISSUE_NUMBER, ANY_OWNER_ID, ANY_IS_PULL_REQUEST);
        given(mockEvent.issue()).willReturn(issue);

        GithubIssue actual = extractor.extractFrom(mockEvent);

        assertEquals(issue, actual);
    }

    @Test(expected = ExtractException.class)
    public void givenAnIssueEvent_whenExtractingThePayload_thenAnExceptionIsThrown() throws Exception {
        given(mockEvent.issue()).willReturn(null);

        extractor.extractFrom(mockEvent);
    }

}
