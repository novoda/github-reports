package com.novoda.github.reports.web.hooks.handler;

import com.novoda.github.reports.data.db.DbEventDataLayer;
import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.web.hooks.classification.ClassificationException;
import com.novoda.github.reports.web.hooks.classification.EventType;
import com.novoda.github.reports.web.hooks.classification.WebhookEventClassifier;
import com.novoda.github.reports.web.hooks.extract.ExtractException;
import com.novoda.github.reports.web.hooks.extract.PullRequestExtractor;
import com.novoda.github.reports.web.hooks.lambda.GithubWebhookEvent;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.MockitoAnnotations.initMocks;

public class PullRequestHandlerTest {

    @Mock
    private WebhookEventClassifier mockEventClassifier;

    @Mock
    private PullRequestExtractor mockPullRequestExtractor;

    @Mock
    private DbEventDataLayer mockEventDataLayer;

    @InjectMocks
    private PullRequestHandler pullRequestHandler;

    @Mock
    private GithubWebhookEvent mockEvent;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void givenAnEventThatIsAPullRequest_whenHandlingIt_thenReturnTrue() throws UnhandledEventException, ClassificationException {
        given(mockEventClassifier.classify(mockEvent)).willReturn(EventType.PULL_REQUEST);
        whenExtractingPullRequestExtractSuccessfuly();

        pullRequestHandler.handle(mockEvent);
    }

    private void whenExtractingPullRequestExtractSuccessfuly() {
        try {
            given(mockPullRequestExtractor.extractFrom(mockEvent)).willReturn(mock(GithubIssue.class));
        } catch (ExtractException e) {
            // nothing to do
        }
    }

    @Test(expected = UnhandledEventException.class)
    public void givenAnEventThatIsAPullRequestButDoesNotHavePayload_whenHandlingIt_thenThrowException()
            throws UnhandledEventException, ClassificationException {

        given(mockEventClassifier.classify(mockEvent)).willReturn(EventType.PULL_REQUEST);
        whenExtractingPullRequestThrowExtractException();

        pullRequestHandler.handle(mockEvent);
    }

    private void whenExtractingPullRequestThrowExtractException() {
        try {
            given(mockPullRequestExtractor.extractFrom(mockEvent)).willThrow(ExtractException.class);
        } catch (ExtractException e) {
            // nothing to do
        }
    }
}
