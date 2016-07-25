package com.novoda.github.reports.web.hooks.handler;

import com.novoda.github.reports.web.hooks.classification.EventType;
import com.novoda.github.reports.web.hooks.classification.WebhookEventClassifier;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.MockitoAnnotations.initMocks;

public class EventForwarderTest {

    private static final EventType ANY_EVENT_TYPE = EventType.PULL_REQUEST;

    @Mock
    private WebhookEventClassifier mockEventClassifier;

    @Mock
    private EventHandler mockHandler;

    private EventForwarder eventForwarder;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        eventForwarder = new EventForwarder(mockEventClassifier, Collections.singletonMap(ANY_EVENT_TYPE, mockHandler));
    }

    @Test
    public void givenAHandlerThatHandlesThisEvent_whenForwardingEvent_thenEventIsHandled() throws Exception {
        GithubWebhookEvent event = mock(GithubWebhookEvent.class);
        given(mockEventClassifier.classify(event)).willReturn(ANY_EVENT_TYPE);

        eventForwarder.forward(event);
    }

    @Test(expected = UnhandledEventException.class)
    public void givenAHandlerThatDoesNotHandleThisEvent_whenRouting_thenExceptionIsThrown() throws UnhandledEventException {
        GithubWebhookEvent event = mock(GithubWebhookEvent.class);

        eventForwarder.forward(event);
    }

}
