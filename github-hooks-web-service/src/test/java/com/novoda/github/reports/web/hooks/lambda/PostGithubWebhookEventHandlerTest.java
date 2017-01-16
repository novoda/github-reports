package com.novoda.github.reports.web.hooks.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.google.gson.JsonObject;
import com.novoda.github.reports.service.GithubUser;
import com.novoda.github.reports.service.issue.GithubComment;
import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.service.repository.GithubRepository;
import com.novoda.github.reports.web.hooks.handler.EventForwarder;
import com.novoda.github.reports.web.hooks.handler.UnhandledEventException;
import com.novoda.github.reports.web.hooks.model.GithubAction;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;
import com.novoda.github.reports.web.hooks.model.GithubWebhookPullRequest;
import com.novoda.github.reports.web.hooks.model.WebhookRequest;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class PostGithubWebhookEventHandlerTest {

    private static final Context ANY_CONTEXT = null;

    @Mock
    private PayloadVerificationRunner mockPayloadVerificationRunner;

    @Mock
    private WebhookRequestExtractor mockWebhookRequestExtractor;

    @Mock
    private EventExtractor mockEventExtractor;

    @Mock
    private EventForwarder mockEventForwarder;

    @Mock
    private OutputWriter mockOutputWriter;

    @Mock
    private Logger mockLogger;

    @Mock
    private OutputStream mockOutputStream;

    @Mock
    private InputStream mockInputStream;

    private PostGithubWebhookEventHandler handler;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        handler = new PostGithubWebhookEventHandler(
                mockPayloadVerificationRunner,
                mockWebhookRequestExtractor,
                mockEventExtractor,
                mockEventForwarder,
                mockOutputWriter,
                mockLogger);
        doThrow(RuntimeException.class).when(mockOutputWriter).outputException(any(Exception.class));
    }

    @Test
    public void givenARequest_whenHandlingIt_thenTheProvidedOutputStreamIsSet() throws Exception {
        WebhookRequest request = givenAWebhookRequest();
        givenAWebhookEventFrom(request);

        handler.handleRequest(mockInputStream, mockOutputStream, ANY_CONTEXT);

        verify(mockOutputWriter).setOutputStream(mockOutputStream);
    }

    @Test
    public void givenARequest_whenHandlingIt_thenWeCheckIfItsPayloadIsValid() throws Exception {
        WebhookRequest request = givenAWebhookRequest();
        givenAWebhookEventFrom(request);

        handler.handleRequest(mockInputStream, mockOutputStream, ANY_CONTEXT);

        verify(mockPayloadVerificationRunner).verify(request);
    }

    @Test
    public void givenAValidRequest_whenHandlingIt_thenTheEventIsForwarded() throws Exception {
        WebhookRequest request = givenAWebhookRequest();
        GithubWebhookEvent event = givenAWebhookEventFrom(request);

        handler.handleRequest(mockInputStream, mockOutputStream, ANY_CONTEXT);

        verify(mockEventForwarder).forward(event);
    }

    @Test(expected = RuntimeException.class)
    public void givenRequestExtractionFails_whenHandlingTheRequest_thenAnExceptionIsOutput() throws Exception {
        doThrow(RuntimeException.class).when(mockWebhookRequestExtractor).extractFrom(mockInputStream);

        handler.handleRequest(mockInputStream, mockOutputStream, ANY_CONTEXT);

    }

    @Test
    public void givenPayloadVerificationFails_whenHandlingTheRequest_thenTheEventIsNotForwarded() throws Exception {
        doThrow(RuntimeException.class).when(mockPayloadVerificationRunner).verify(any(WebhookRequest.class));

        try {
            handler.handleRequest(mockInputStream, mockOutputStream, ANY_CONTEXT);
        } catch (RuntimeException e) {
            // we're throwing this ourselves
        } finally {
            verify(mockEventForwarder, never()).forward(any(GithubWebhookEvent.class));
        }
    }

    @Test(expected = RuntimeException.class)
    public void givenPayloadVerificationFails_whenHandlingTheRequest_thenAnExceptionIsOutput() throws Exception {
        doThrow(RuntimeException.class).when(mockPayloadVerificationRunner).verify(any(WebhookRequest.class));

        handler.handleRequest(mockInputStream, mockOutputStream, ANY_CONTEXT);

    }

    @Test(expected = RuntimeException.class)
    public void givenEventExtractionFails_whenHandlingTheRequest_thenAnExceptionIsOutput() throws Exception {
        doThrow(RuntimeException.class).when(mockEventExtractor).extractFrom(any(WebhookRequest.class));

        handler.handleRequest(mockInputStream, mockOutputStream, ANY_CONTEXT);

    }

    @Test(expected = RuntimeException.class)
    public void givenEventForwardingFails_whenHandlingTheRequest_thenAnExceptionIsOutput() throws Exception {
        doThrow(UnhandledEventException.class).when(mockEventForwarder).forward(any(GithubWebhookEvent.class));

        handler.handleRequest(mockInputStream, mockOutputStream, ANY_CONTEXT);

    }

    private WebhookRequest givenAWebhookRequest() {
        WebhookRequest webhookRequest = aWebhookRequest();
        given(mockWebhookRequestExtractor.extractFrom(mockInputStream)).willReturn(webhookRequest);
        return webhookRequest;
    }

    private WebhookRequest aWebhookRequest() {
        return WebhookRequest.builder()
                .body(new JsonObject())
                .headers(new HashMap<>())
                .build();
    }

    private GithubWebhookEvent givenAWebhookEventFrom(WebhookRequest webhookRequest) {
        GithubWebhookEvent webhookEvent = aWebhookEvent();
        given(mockEventExtractor.extractFrom(webhookRequest)).willReturn(webhookEvent);
        return webhookEvent;
    }

    private GithubWebhookEvent aWebhookEvent() {
        return GithubWebhookEvent.builder()
                .action(GithubAction.ADDED)
                .number(23)
                .sender(mock(GithubUser.class))
                .pullRequest(mock(GithubWebhookPullRequest.class))
                .issue(mock(GithubIssue.class))
                .repository(mock(GithubRepository.class))
                .comment(mock(GithubComment.class))
                .build();
    }

}
