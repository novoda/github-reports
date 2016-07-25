package com.novoda.github.reports.web.hooks.handler;

import com.novoda.github.reports.data.db.DbEventDataLayer;
import com.novoda.github.reports.web.hooks.classification.EventType;
import com.novoda.github.reports.web.hooks.extract.ExtractException;
import com.novoda.github.reports.web.hooks.extract.PullRequestExtractor;
import com.novoda.github.reports.web.hooks.lambda.GithubWebhookEvent;
import com.novoda.github.reports.web.hooks.classification.WebhookEventClassifier;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PullRequestHandlerTest {

    private static final EventType ANY_EVENT_TYPE_BUT_PULL_REQUEST = EventType.COMMIT_COMMENT;

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
    public void givenAnEventThatsNotAPullRequest_whenHandlingIt_thenReturnFalse() throws UnhandledEventException {
        when(mockEventClassifier.classify(mockEvent)).thenReturn(ANY_EVENT_TYPE_BUT_PULL_REQUEST);

        boolean eventHandled = pullRequestHandler.handle(mockEvent);

        assertEquals(false, eventHandled);
    }

    @Test
    public void givenAnEventThatIsAPullRequest_whenHandlingIt_thenReturnTrue() throws UnhandledEventException {
        when(mockEventClassifier.classify(mockEvent)).thenReturn(EventType.PULL_REQUEST);

        boolean eventHandled = pullRequestHandler.handle(mockEvent);

        assertEquals(true, eventHandled);
    }

    @Test(expected = UnhandledEventException.class)
    public void givenAnEventThatIsAPullRequestButDoesNotHavePayload_whenHandlingIt_thenReturnTrue() throws UnhandledEventException {
        when(mockEventClassifier.classify(mockEvent)).thenReturn(EventType.PULL_REQUEST);
        whenExtractingPullRequestThrowExtractException();

        pullRequestHandler.handle(mockEvent);
    }

    private void whenExtractingPullRequestThrowExtractException() {
        try {
            when(mockPullRequestExtractor.extractFrom(mockEvent)).thenThrow(ExtractException.class);
        } catch (ExtractException e) {
            // nothing to do
        }
    }

}
