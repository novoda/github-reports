package com.novoda.github.reports.web.hooks.extract;

import com.novoda.github.reports.service.issue.GithubComment;
import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;
import com.novoda.github.reports.web.hooks.model.IssueComment;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.initMocks;

public class IssueCommentExtractorTest {

    private static final int ANY_ISSUE_NUMBER = 23;
    private static final long ANY_OWNER_ID = 88;
    private static final boolean ANY_IS_PULL_REQUEST = false;

    @Mock
    private GithubWebhookEvent mockEvent;

    @InjectMocks
    private IssueCommentExtractor extractor;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void givenAnIssueCommentEvent_whenExtractingThePayload_thenItIsExtracted() throws Exception {
        GithubComment comment = new GithubComment();
        GithubIssue issue = new GithubIssue(ANY_ISSUE_NUMBER, ANY_OWNER_ID, ANY_IS_PULL_REQUEST);
        given(mockEvent.issue()).willReturn(issue);
        given(mockEvent.comment()).willReturn(comment);

        IssueComment actual = extractor.extractFrom(mockEvent);

        assertEquals(comment, actual.getComment());
        assertEquals(issue, actual.getIssue());
    }

    @Test(expected = ExtractException.class)
    public void givenAnIssueCommentEvent_whenExtractingThePayload_thenAnExceptionIsThrown() throws Exception {
        given(mockEvent.comment()).willReturn(null);

        extractor.extractFrom(mockEvent);
    }

}
