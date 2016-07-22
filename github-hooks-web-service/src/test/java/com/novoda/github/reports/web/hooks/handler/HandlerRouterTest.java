package com.novoda.github.reports.web.hooks.handler;

import com.novoda.github.reports.web.hooks.lambda.GithubWebhookEvent;
import com.novoda.github.reports.web.hooks.parse.EventHandler;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class HandlerRouterTest {

    @Mock
    private EventHandler mockHandler;

    private HandlerRouter handlerRouter;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        handlerRouter = new HandlerRouter(Collections.singletonList(mockHandler));
    }

    @Test
    public void givenAHandlerThatHandlesThisEvent_whenRouting_thenEventIsHandled() throws Exception {
        GithubWebhookEvent event = mock(GithubWebhookEvent.class);
        when(mockHandler.handle(event)).thenReturn(true);

        handlerRouter.route(event);
    }

    @Test(expected = HandlerRouter.UnhandledEventException.class)
    public void givenAHandlerThatDoesNotHandleThisEvent_whenRouting_thenExceptionIsThrown() throws HandlerRouter.UnhandledEventException {
        GithubWebhookEvent event = mock(GithubWebhookEvent.class);
        when(mockHandler.handle(event)).thenReturn(false);

        handlerRouter.route(event);
    }

}
