package com.novoda.github.reports.web.hooks.extract;

import com.novoda.github.reports.service.GithubUser;
import com.novoda.github.reports.service.issue.GithubComment;
import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.service.repository.GithubRepository;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;
import com.novoda.github.reports.web.hooks.model.GithubWebhookPullRequest;
import com.novoda.github.reports.web.hooks.model.PullRequest;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.initMocks;

public class PullRequestExtractorTest {

    private static final int ANY_OWNER_ID = 2;
    private static final long ANY_REPO_ID = 1L;
    private static final GithubComment NO_COMMENT = null;
    private static final long ANY_ISSUE_ID = 23L;
    private static final Date ANY_DATE = new Date();
    private static final boolean ANY_WAS_MERGED = false;

    @Mock
    private GithubWebhookEvent mockEvent;

    @InjectMocks
    private PullRequestExtractor extractor;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void givenAPullRequestEvent_whenExtractingThePayload_thenItIsExtracted() throws Exception {
        GithubRepository repository = new GithubRepository(ANY_REPO_ID);
        GithubUser user = new GithubUser(ANY_OWNER_ID);
        GithubWebhookPullRequest issue = new GithubWebhookPullRequest(ANY_ISSUE_ID, ANY_DATE, user, ANY_WAS_MERGED);
        given(mockEvent.pullRequest()).willReturn(issue);
        given(mockEvent.repository()).willReturn(repository);

        PullRequest actual = extractor.extractFrom(mockEvent);

        assertEquals(repository, actual.getRepository());
        assertEquals(issue, actual.getIssue());
    }

    @Test
    public void givenAPullRequesEvent_whenExtractingTheIssue_thenItIsMarkedAsAPullRequest() throws Exception {
        GithubRepository repository = new GithubRepository(ANY_REPO_ID);
        GithubUser user = new GithubUser(ANY_OWNER_ID);
        GithubWebhookPullRequest issue = new GithubWebhookPullRequest(ANY_ISSUE_ID, ANY_DATE, user, ANY_WAS_MERGED);
        given(mockEvent.pullRequest()).willReturn(issue);
        given(mockEvent.repository()).willReturn(repository);

        GithubIssue actualIssue = extractor.extractFrom(mockEvent).getIssue();

        assertTrue(actualIssue.isPullRequest());
    }

    @Test(expected = ExtractException.class)
    public void givenAReviewCommentEvent_whenExtractingThePayload_thenAnExceptionIsThrown() throws Exception {
        given(mockEvent.comment()).willReturn(NO_COMMENT);

        extractor.extractFrom(mockEvent);
    }

}
