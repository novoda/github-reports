package com.novoda.github.reports.web.hooks.extract;

import com.novoda.github.reports.service.GithubUser;
import com.novoda.github.reports.service.issue.GithubComment;
import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;
import com.novoda.github.reports.web.hooks.model.GithubWebhookPullRequest;
import com.novoda.github.reports.web.hooks.model.ReviewComment;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.initMocks;

public class ReviewCommentExtractorTest {

    private static final long ANY_OWNER_ID = 88;
    private static final Date ANY_DATE = new Date();
    private static final long ANY_ISSUE_ID = 43L;
    private static final boolean ANY_WAS_MERGED = false;

    @Mock
    private GithubWebhookEvent mockEvent;

    @InjectMocks
    private ReviewCommentExtractor extractor;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void givenAReviewCommentEvent_whenExtractingThePayload_thenItIsExtracted() throws Exception {
        GithubComment comment = new GithubComment();
        GithubUser user = new GithubUser(ANY_OWNER_ID);
        GithubWebhookPullRequest issue = new GithubWebhookPullRequest(ANY_ISSUE_ID, ANY_DATE, user, ANY_WAS_MERGED);
        given(mockEvent.pullRequest()).willReturn(issue);
        given(mockEvent.comment()).willReturn(comment);

        ReviewComment actual = extractor.extractFrom(mockEvent);

        assertEquals(comment, actual.getComment());
        assertEquals(issue, actual.getIssue());
    }

    @Test
    public void givenAReviewCommentEvent_whenExtractingTheIssue_thenItIsMarkedAsAPullRequest() throws Exception {
        GithubComment comment = new GithubComment();
        GithubUser user = new GithubUser(ANY_OWNER_ID);
        GithubWebhookPullRequest issue = new GithubWebhookPullRequest(ANY_ISSUE_ID, ANY_DATE, user, ANY_WAS_MERGED);
        given(mockEvent.pullRequest()).willReturn(issue);
        given(mockEvent.comment()).willReturn(comment);

        GithubIssue actualIssue = extractor.extractFrom(mockEvent).getIssue();

        assertTrue(actualIssue.isPullRequest());
    }

    @Test(expected = ExtractException.class)
    public void givenAReviewCommentEvent_whenExtractingThePayload_thenAnExceptionIsThrown() throws Exception {
        given(mockEvent.comment()).willReturn(null);

        extractor.extractFrom(mockEvent);
    }

}
