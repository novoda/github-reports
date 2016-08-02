package com.novoda.github.reports.web.hooks.classification;

import com.novoda.github.reports.service.issue.GithubComment;
import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;
import com.novoda.github.reports.web.hooks.model.GithubWebhookPullRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.MockitoAnnotations.initMocks;

public class WebhookEventClassifierTest {

    @Mock
    private ClassificationRule mockRule;

    @Mock
    private GithubWebhookEvent mockEvent;

    @InjectMocks
    private WebhookEventClassifier eventClassifier;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test(expected = ClassificationException.class)
    public void givenANonClassifiableEvent_whenClassifying_thenThrowsException() throws ClassificationException {
        given(mockRule.check(mockEvent)).willReturn(false);

        eventClassifier.classify(mockEvent);
    }

    @Test
    public void givenAPullRequestEvent_whenClassifyingIt_thenWeGetItsType() throws Exception {
        given(mockRule.check(mockEvent)).willReturn(true);
        given(mockEvent.pullRequest()).willReturn(mock(GithubWebhookPullRequest.class));

        EventType actual = eventClassifier.classify(mockEvent);

        assertEquals(EventType.PULL_REQUEST, actual);
    }

    @Test
    public void givenACommitCommentEvent_whenClassifyingIt_thenWeGetItsType() throws Exception {
        given(mockRule.check(mockEvent)).willReturn(true);
        given(mockEvent.comment()).willReturn(mock(GithubComment.class));

        EventType actual = eventClassifier.classify(mockEvent);

        assertEquals(EventType.COMMIT_COMMENT, actual);
    }

    @Test
    public void givenAnIssueEvent_whenClassifyingIt_thenWeGetItsType() throws Exception {
        given(mockRule.check(mockEvent)).willReturn(true);
        given(mockEvent.issue()).willReturn(mock(GithubIssue.class));

        EventType actual = eventClassifier.classify(mockEvent);

        assertEquals(EventType.ISSUE, actual);
    }

    @Test
    public void givenAnIssueCommentEvent_whenClassifyingIt_thenWeGetItsType() throws Exception {
        given(mockRule.check(mockEvent)).willReturn(true);
        given(mockEvent.issue()).willReturn(mock(GithubIssue.class));
        given(mockEvent.comment()).willReturn(mock(GithubComment.class));

        EventType actual = eventClassifier.classify(mockEvent);

        assertEquals(EventType.ISSUE_COMMENT, actual);
    }

    @Test
    public void givenAReviewCommentEvent_whenClassifyingIt_thenWeGetItsType() throws Exception {
        given(mockRule.check(mockEvent)).willReturn(true);
        given(mockEvent.pullRequest()).willReturn(mock(GithubWebhookPullRequest.class));
        given(mockEvent.comment()).willReturn(mock(GithubComment.class));

        EventType actual = eventClassifier.classify(mockEvent);

        assertEquals(EventType.DEPRECATED_REVIEW_COMMENT, actual);
    }
}
