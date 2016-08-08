package com.novoda.github.reports.web.hooks.extract;

import com.novoda.github.reports.service.GithubUser;
import com.novoda.github.reports.service.repository.GithubRepository;
import com.novoda.github.reports.web.hooks.model.GithubAction;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;
import com.novoda.github.reports.web.hooks.model.GithubWebhookPullRequest;
import com.novoda.github.reports.web.hooks.model.PullRequest;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.initMocks;

public class PullRequestExtractorTest {

    private static final long ANY_ISSUE_ID = 23L;
    private static final Date ANY_DATE = new Date();
    private static final long ANY_REPOSITORY_ID = 88L;
    private static final boolean ANY_WAS_MERGED = false;
    private static final long ANY_USER_ID = 86L;
    private static final String ANY_USERNAME = "pirata";
    private static final String ANY_REPOSITORY_NAME = "presunto";
    private static final boolean ANY_IS_PRIVATE_REPOSITORY = false;
    private static final GithubAction ANY_ACTION = GithubAction.OPENED;

    @Mock
    private GithubWebhookEvent mockEvent;

    @InjectMocks
    private PullRequestExtractor extractor;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void givenAPullRequestEvent_whenExtractingTheIssue_thenItIsMarkedAsAPullRequest() throws Exception {
        PullRequest expected = givenAPullRequest();

        PullRequest actual = extractor.extractFrom(mockEvent);

        assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    private PullRequest givenAPullRequest() {
        GithubUser githubUser = new GithubUser(ANY_USER_ID, ANY_USERNAME);
        GithubWebhookPullRequest webhookPullRequest = new GithubWebhookPullRequest(ANY_ISSUE_ID, ANY_DATE, githubUser, ANY_WAS_MERGED);
        GithubRepository githubRepository = new GithubRepository(ANY_REPOSITORY_ID, ANY_REPOSITORY_NAME, ANY_IS_PRIVATE_REPOSITORY);
        given(mockEvent.pullRequest()).willReturn(webhookPullRequest);
        given(mockEvent.repository()).willReturn(githubRepository);
        given(mockEvent.action()).willReturn(ANY_ACTION);
        return new PullRequest(webhookPullRequest, githubRepository, ANY_ACTION);
    }

    @Test(expected = ExtractException.class)
    public void givenAPullRequestEventWithNoComment_whenExtractingThePayload_thenAnExceptionIsThrown() throws Exception {
        given(mockEvent.comment()).willReturn(null);

        extractor.extractFrom(mockEvent);
    }

    @Test(expected = ExtractException.class)
    public void givenAPullRequestEventWithNoPullRequest_whenExtractingThePayload_thenAnExceptionIsThrown() throws Exception {
        given(mockEvent.pullRequest()).willReturn(null);

        extractor.extractFrom(mockEvent);
    }

}
