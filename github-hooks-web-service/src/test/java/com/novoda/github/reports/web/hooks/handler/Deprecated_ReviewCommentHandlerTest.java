package com.novoda.github.reports.web.hooks.handler;

import com.novoda.github.reports.web.hooks.classification.EventType;
import com.novoda.github.reports.web.hooks.extract.ExtractException;
import com.novoda.github.reports.web.hooks.extract.Deprecated_ReviewCommentExtractor;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class Deprecated_ReviewCommentHandlerTest {

    @Mock
    private Deprecated_ReviewCommentExtractor mockExtractor;

    @InjectMocks
    private Deprecated_ReviewCommentHandler deprecatedReviewCommentHandler;

    @Mock
    private GithubWebhookEvent mockEvent;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void givenAnEvent_whenHandlingIt_thenThePayloadIsExtracted() throws Exception {

        deprecatedReviewCommentHandler.handle(mockEvent);

        verify(mockExtractor).extractFrom(mockEvent);
    }

    @Test(expected = UnhandledEventException.class)
    public void givenAnEventThatIsNotAReviewComment_whenHandlingIt_thenThrowsException() throws Exception {
        given(mockExtractor.extractFrom(mockEvent)).willThrow(ExtractException.class);

        deprecatedReviewCommentHandler.handle(mockEvent);
    }

    @Test
    public void handledEventTypeShouldBeReviewComment() {
        assertEquals(EventType.DEPRECATED_REVIEW_COMMENT, deprecatedReviewCommentHandler.handledEventType());
    }

}
