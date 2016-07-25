package com.novoda.github.reports.web.hooks.handler;

import com.novoda.github.reports.web.hooks.lambda.GithubWebhookEvent;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class HandlerForwarderTest {

    @Mock
    private EventHandler mockHandler;

    private HandlerForwarder handlerForwarder;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        handlerForwarder = new HandlerForwarder(Collections.singletonList(mockHandler));
    }

    @Test
    public void givenAHandlerThatHandlesThisEvent_whenRouting_thenEventIsHandled() throws Exception {
        GithubWebhookEvent event = mock(GithubWebhookEvent.class);
        when(mockHandler.handle(event)).thenReturn(true);

        handlerForwarder.route(event);
    }

    @Test(expected = UnhandledEventException.class)
    public void givenAHandlerThatDoesNotHandleThisEvent_whenRouting_thenExceptionIsThrown() throws UnhandledEventException {
        GithubWebhookEvent event = mock(GithubWebhookEvent.class);
        when(mockHandler.handle(event)).thenReturn(false);

        handlerForwarder.route(event);
    }

}
