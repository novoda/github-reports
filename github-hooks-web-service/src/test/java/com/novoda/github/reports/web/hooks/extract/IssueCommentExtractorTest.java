package com.novoda.github.reports.web.hooks.extract;

import com.novoda.github.reports.service.GithubUser;
import com.novoda.github.reports.service.issue.GithubComment;
import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.service.repository.GithubRepository;
import com.novoda.github.reports.web.hooks.model.GithubAction;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;
import com.novoda.github.reports.web.hooks.model.IssueComment;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.initMocks;

public class IssueCommentExtractorTest {

    private static final int ANY_ISSUE_NUMBER = 23;
    private static final boolean ANY_IS_PULL_REQUEST = false;
    private static final long ANY_USER_ID = 88;
    private static final long ANY_REPOSITORY_ID = 42L;
    private static final long ANY_COMMENT_ID = 23L;
    private static final Date ANY_DATE = new Date();

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
        IssueComment expected = givenAnIssueComment();

        IssueComment actual = extractor.extractFrom(mockEvent);

        assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    private IssueComment givenAnIssueComment() {
        GithubRepository githubRepository = new GithubRepository(ANY_REPOSITORY_ID);
        GithubIssue githubIssue = new GithubIssue(ANY_ISSUE_NUMBER, ANY_USER_ID, ANY_IS_PULL_REQUEST);
        GithubUser githubUser = new GithubUser(ANY_USER_ID);
        GithubComment githubComment = new GithubComment(ANY_COMMENT_ID, githubUser, ANY_DATE);
        given(mockEvent.comment()).willReturn(githubComment);
        given(mockEvent.repository()).willReturn(githubRepository);
        given(mockEvent.issue()).willReturn(githubIssue);
        given(mockEvent.action()).willReturn(GithubAction.OPENED);
        return new IssueComment(githubComment, githubRepository, githubIssue, GithubAction.OPENED);
    }

    @Test(expected = ExtractException.class)
    public void givenAnIssueCommentEvent_whenExtractingThePayload_thenAnExceptionIsThrown() throws Exception {
        given(mockEvent.comment()).willReturn(null);

        extractor.extractFrom(mockEvent);
    }

}
