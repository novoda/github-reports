package com.novoda.github.reports.web.hooks.handler;

import com.novoda.github.reports.web.hooks.classification.ClassificationException;
import com.novoda.github.reports.web.hooks.classification.EventType;
import com.novoda.github.reports.web.hooks.extract.ExtractException;
import com.novoda.github.reports.web.hooks.extract.PullRequestExtractor;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;
import com.novoda.github.reports.web.hooks.model.PullRequest;
import com.novoda.github.reports.web.hooks.persistence.PersistenceException;
import com.novoda.github.reports.web.hooks.persistence.PullRequestPersister;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class PullRequestHandlerTest {

    @Mock
    private PullRequestExtractor mockPullRequestExtractor;

    @Mock
    private PullRequestPersister mockPersister;

    @InjectMocks
    private PullRequestHandler pullRequestHandler;

    @Mock
    private GithubWebhookEvent mockEvent;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void givenAnEventThatIsAPullRequest_whenHandlingIt_thenWeDelegateToExtractor() throws ExtractException, UnhandledEventException {

        pullRequestHandler.handle(mockEvent);

        verify(mockPullRequestExtractor).extractFrom(mockEvent);
    }

    @Test
    public void givenAnEvent_whenHandlingIt_thenWeDelegateToPersister() throws UnhandledEventException, PersistenceException {

        pullRequestHandler.handle(mockEvent);

        verify(mockPersister).persist(any(PullRequest.class));
    }

    @Test(expected = UnhandledEventException.class)
    public void givenAnErroneousEvent_whenHandlingIt_thenThrowException()
            throws UnhandledEventException, ClassificationException {

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

    @Test
    public void handledEventTypeShouldBePullRequest() {
        assertEquals(EventType.PULL_REQUEST, pullRequestHandler.handledEventType());
    }
}
