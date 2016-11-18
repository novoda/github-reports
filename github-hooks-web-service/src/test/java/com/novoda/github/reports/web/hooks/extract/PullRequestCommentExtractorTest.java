package com.novoda.github.reports.web.hooks.extract;

import com.novoda.github.reports.service.GithubUser;
import com.novoda.github.reports.service.issue.GithubComment;
import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.service.repository.GithubRepository;
import com.novoda.github.reports.web.hooks.model.GithubAction;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;
import com.novoda.github.reports.web.hooks.model.PullRequestComment;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.initMocks;

public class PullRequestCommentExtractorTest {

    private static final int ANY_PULL_REQUEST_NUMBER = 23;
    private static final boolean IS_PULL_REQUEST = true;
    private static final long ANY_USER_ID = 88;
    private static final long ANY_REPOSITORY_ID = 42L;
    private static final long ANY_COMMENT_ID = 23L;
    private static final Date ANY_DATE = new Date();
    private static final GithubAction ANY_ACTION = GithubAction.OPENED;

    @Mock
    private GithubWebhookEvent mockEvent;

    @InjectMocks
    private PullRequestCommentExtractor extractor;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void givenAPullRequestCommentEvent_whenExtractingThePayload_thenItIsExtracted() throws Exception {
        PullRequestComment expected = givenAPullRequestComment();

        PullRequestComment actual = extractor.extractFrom(mockEvent);

        assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    private PullRequestComment givenAPullRequestComment() {
        GithubRepository githubRepository = new GithubRepository(ANY_REPOSITORY_ID);
        GithubIssue githubIssue = new GithubIssue(ANY_PULL_REQUEST_NUMBER, ANY_USER_ID, IS_PULL_REQUEST);
        GithubUser githubUser = new GithubUser(ANY_USER_ID);
        GithubComment githubComment = new GithubComment(ANY_COMMENT_ID, githubUser, ANY_DATE);
        given(mockEvent.comment()).willReturn(githubComment);
        given(mockEvent.repository()).willReturn(githubRepository);
        given(mockEvent.issue()).willReturn(githubIssue);
        given(mockEvent.action()).willReturn(ANY_ACTION);
        return new PullRequestComment(githubComment, githubRepository, githubIssue, ANY_ACTION);
    }

    @Test(expected = ExtractException.class)
    public void givenAPullRequestCommentEventWithNoComment_whenExtractingThePayload_thenAnExceptionIsThrown() throws Exception {
        given(mockEvent.comment()).willReturn(null);

        extractor.extractFrom(mockEvent);
    }

    @Test(expected = ExtractException.class)
    public void givenAPullRequestCommentEventWithNoIssue_whenExtractingThePayload_thenAnExceptionIsThrown() throws Exception {
        given(mockEvent.issue()).willReturn(null);

        extractor.extractFrom(mockEvent);
    }

    @Test(expected = ExtractException.class)
    public void givenAPullRequestCommentEventWithNoRepository_whenExtractingThePayload_thenAnExceptionIsThrown() throws Exception {
        given(mockEvent.repository()).willReturn(null);

        extractor.extractFrom(mockEvent);
    }

    @Test(expected = ExtractException.class)
    public void givenAPullRequestCommentEventWithIssueNotAPullRequest_whenExtractingThePayload_thenAnExceptionIsThrown() throws Exception {
        GithubIssue githubIssue = new GithubIssue(ANY_PULL_REQUEST_NUMBER, ANY_USER_ID, false);
        given(mockEvent.issue()).willReturn(githubIssue);

        extractor.extractFrom(mockEvent);
    }

}
