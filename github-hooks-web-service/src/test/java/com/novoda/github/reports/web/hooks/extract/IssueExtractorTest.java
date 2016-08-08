package com.novoda.github.reports.web.hooks.extract;

import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.service.repository.GithubRepository;
import com.novoda.github.reports.web.hooks.model.GithubAction;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;
import com.novoda.github.reports.web.hooks.model.Issue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.initMocks;

public class IssueExtractorTest {

    private static final int ANY_ISSUE_NUMBER = 23;
    private static final long ANY_OWNER_ID = 88;
    private static final boolean ANY_IS_PULL_REQUEST = false;
    private static final long ANY_REPOSITORY_ID = 42L;
    private static final GithubAction ANY_ACTION = GithubAction.OPENED;

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
        Issue expected = givenAnIssue();

        Issue actual = extractor.extractFrom(mockEvent);

        assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    private Issue givenAnIssue() {
        GithubIssue githubIssue = new GithubIssue(ANY_ISSUE_NUMBER, ANY_OWNER_ID, ANY_IS_PULL_REQUEST);
        GithubRepository githubRepository = new GithubRepository(ANY_REPOSITORY_ID);
        given(mockEvent.issue()).willReturn(githubIssue);
        given(mockEvent.repository()).willReturn(githubRepository);
        given(mockEvent.action()).willReturn(ANY_ACTION);
        return new Issue(githubIssue, githubRepository, ANY_ACTION);
    }

    @Test(expected = ExtractException.class)
    public void givenAnIssueEventWithNoIssue_whenExtractingThePayload_thenAnExceptionIsThrown() throws Exception {
        given(mockEvent.issue()).willReturn(null);

        extractor.extractFrom(mockEvent);
    }

    @Test(expected = ExtractException.class)
    public void givenAnIssueEventWithNoRepository_whenExtractingThePayload_thenAnExceptionIsThrown() throws Exception {
        given(mockEvent.repository()).willReturn(null);

        extractor.extractFrom(mockEvent);
    }

}
