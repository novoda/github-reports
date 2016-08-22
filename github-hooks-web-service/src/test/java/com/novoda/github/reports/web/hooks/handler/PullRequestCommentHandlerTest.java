package com.novoda.github.reports.web.hooks.handler;

import com.novoda.github.reports.web.hooks.classification.EventType;
import com.novoda.github.reports.web.hooks.extract.ExtractException;
import com.novoda.github.reports.web.hooks.extract.PullRequestCommentExtractor;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;
import com.novoda.github.reports.web.hooks.persistence.PullRequestCommentPersister;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class PullRequestCommentHandlerTest {

    @Mock
    private PullRequestCommentExtractor mockExtractor;

    @Mock
    private PullRequestCommentPersister mockPersister;

    @Mock
    private GithubWebhookEvent mockEvent;

    @InjectMocks
    private PullRequestCommentHandler pullRequestCommentHandler;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void givenAnEvent_whenHandlingIt_thenThePayloadIsExtracted() throws Exception {

        pullRequestCommentHandler.handle(mockEvent);

        verify(mockExtractor).extractFrom(mockEvent);
    }

    @Test(expected = UnhandledEventException.class)
    public void givenAnEventThatIsNotAPullRequestComment_whenHandlingIt_thenThrowsException() throws Exception {
        given(mockExtractor.extractFrom(mockEvent)).willThrow(ExtractException.class);

        pullRequestCommentHandler.handle(mockEvent);
    }

    @Test
    public void handledEventTypeShouldBeIssueComment() {
        assertEquals(EventType.PULL_REQUEST_COMMENT, pullRequestCommentHandler.handledEventType());
    }

}
