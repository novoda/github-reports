package com.novoda.github.reports.web.hooks.extract;

import com.novoda.github.reports.service.GithubUser;
import com.novoda.github.reports.service.issue.GithubComment;
import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;
import com.novoda.github.reports.web.hooks.model.GithubWebhookPullRequest;
import com.novoda.github.reports.web.hooks.model.CommitComment;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.MockitoAnnotations.initMocks;

public class CommitCommentExtractorTest {

    private static final long ANY_OWNER_ID = 88;
    private static final Date ANY_DATE = new Date();
    private static final long ANY_ISSUE_ID = 43L;
    private static final boolean ANY_WAS_MERGED = false;

    @Mock
    private GithubWebhookEvent mockEvent;

    @InjectMocks
    private CommitCommentExtractor extractor;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void givenACommitCommentEvent_whenExtractingThePayload_thenItIsExtracted() throws Exception {
        GithubComment comment = mock(GithubComment.class);
        GithubUser user = new GithubUser(ANY_OWNER_ID);
        GithubWebhookPullRequest webhookPullRequest = new GithubWebhookPullRequest(ANY_ISSUE_ID, ANY_DATE, user, ANY_WAS_MERGED);
        given(mockEvent.pullRequest()).willReturn(webhookPullRequest);
        given(mockEvent.comment()).willReturn(comment);

        CommitComment actual = extractor.extractFrom(mockEvent);

        assertEquals(comment, actual.getComment());
        assertEquals(webhookPullRequest, actual.getWebhookPullRequest());
    }

    @Test
    public void givenACommitCommentEvent_whenExtractingTheIssue_thenItIsMarkedAsAPullRequest() throws Exception {
        GithubComment comment = mock(GithubComment.class);
        GithubUser user = new GithubUser(ANY_OWNER_ID);
        GithubWebhookPullRequest webhookPullRequest = new GithubWebhookPullRequest(ANY_ISSUE_ID, ANY_DATE, user, ANY_WAS_MERGED);
        given(mockEvent.pullRequest()).willReturn(webhookPullRequest);
        given(mockEvent.comment()).willReturn(comment);

        GithubIssue actualIssue = extractor.extractFrom(mockEvent).getWebhookPullRequest();

        assertTrue(actualIssue.isPullRequest());
    }

    @Test(expected = ExtractException.class)
    public void givenACommitCommentEvent_whenExtractingThePayload_thenAnExceptionIsThrown() throws Exception {
        given(mockEvent.comment()).willReturn(null);

        extractor.extractFrom(mockEvent);
    }

}
