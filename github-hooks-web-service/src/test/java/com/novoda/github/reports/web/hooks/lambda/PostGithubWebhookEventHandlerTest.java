package com.novoda.github.reports.web.hooks.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.util.StringInputStream;
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
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
    }

    @Test
    public void givenARequest_whenHandlingIt_thenTheProvidedOutputStreamIsSet() throws Exception {
        OutputStream outputStream = mock(OutputStream.class);
        InputStream inputStream = mock(InputStream.class);
        WebhookRequest webhookRequest = aWebhookRequest();
        given(mockWebhookRequestExtractor.extractFrom(inputStream)).willReturn(webhookRequest);
        given(mockEventExtractor.extractFrom(webhookRequest)).willReturn(aWebhookEvent());

        handler.handleRequest(inputStream, outputStream, ANY_CONTEXT);

        verify(mockOutputWriter).setOutputStream(outputStream);
    }

    @Test
    public void givenARequest_whenHandlingIt_thenWeCheckIfItsPayloadIsValid() throws Exception {
        OutputStream outputStream = mock(OutputStream.class);
        InputStream inputStream = mock(InputStream.class);
        WebhookRequest webhookRequest = aWebhookRequest();
        given(mockWebhookRequestExtractor.extractFrom(inputStream)).willReturn(webhookRequest);
        given(mockEventExtractor.extractFrom(webhookRequest)).willReturn(aWebhookEvent());

        handler.handleRequest(inputStream, outputStream, ANY_CONTEXT);

        verify(mockPayloadVerificationRunner).verify(webhookRequest);
    }

    @Test
    public void givenAValidRequest_whenHandlingIt_thenTheEventIsForwarded() throws Exception {
        OutputStream outputStream = mock(OutputStream.class);
        InputStream inputStream = mock(InputStream.class);
        WebhookRequest webhookRequest = aWebhookRequest();
        GithubWebhookEvent webhookEvent = aWebhookEvent();
        given(mockWebhookRequestExtractor.extractFrom(inputStream)).willReturn(webhookRequest);
        given(mockEventExtractor.extractFrom(webhookRequest)).willReturn(webhookEvent);

        handler.handleRequest(inputStream, outputStream, ANY_CONTEXT);

        verify(mockEventForwarder).forward(webhookEvent);
    }

    @Test
    public void givenARequestWithInvalidSignature_whenHandlingIt_thenTheEventIsNotForwarded() throws Exception {
        OutputStream outputStream = mock(OutputStream.class);
        InputStream inputStream = mock(InputStream.class);
        doThrow(RuntimeException.class).when(mockPayloadVerificationRunner).verify(any(WebhookRequest.class));

        try {
            handler.handleRequest(inputStream, outputStream, ANY_CONTEXT);
        } catch (RuntimeException e) {
            // we're throwing this ourselves
        } finally {
            verify(mockEventForwarder, never()).forward(any(GithubWebhookEvent.class));
        }
    }

    @Test
    public void givenAValidReopenedRequest_whenHandlingIt_thenTheEventIsNotForwarded() throws Exception {
        String json = readFile("reopened_action.json");
        willThrow(UnhandledEventException.class).given(mockEventForwarder).forward(any(GithubWebhookEvent.class));

        handler.handleRequest(new StringInputStream(json), mock(OutputStream.class), ANY_CONTEXT);

        verify(mockOutputWriter).outputException(any(UnhandledEventException.class));
    }

    @Test(expected = RuntimeException.class)
    public void givenARequestWithoutBody_whenHandlingIt_thenAnExceptionIsThrown() throws Exception {
        String json = readFile("no_body.json");
        doThrow(RuntimeException.class).when(mockOutputWriter).outputException(any(Exception.class));

        handler.handleRequest(new StringInputStream(json), mock(OutputStream.class), ANY_CONTEXT);

    }

    @Test
    public void givenAnInvalidRequest_whenHandlingIt_thenAnExceptionIsOutput() throws Exception {
        String json = readFile("invalid_request.json");
        ArgumentCaptor<Exception> exceptionArgumentCaptor = givenThrownExceptionIsCaptured();

        try {
            handler.handleRequest(new StringInputStream(json), mock(OutputStream.class), ANY_CONTEXT);
        } catch (Exception e) {
            // ignored
        }

        verify(mockOutputWriter).outputException(exceptionArgumentCaptor.getValue());
    }

    @Test
    public void givenAnInvalidAction_whenHandlingIt_thenAnExceptionIsOutput() throws Exception {
        String json = readFile("invalid_action.json");
        ArgumentCaptor<Exception> exceptionArgumentCaptor = givenThrownExceptionIsCaptured();

        try {
            handler.handleRequest(new StringInputStream(json), mock(OutputStream.class), ANY_CONTEXT);
        } catch (Exception e) {
            // ignored
        }

        verify(mockOutputWriter).outputException(exceptionArgumentCaptor.getValue());
    }


    private WebhookRequest aWebhookRequest() {
        return WebhookRequest.builder()
                .body(new JsonObject())
                .headers(new HashMap<>())
                .build();
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


    private ArgumentCaptor<Exception> givenThrownExceptionIsCaptured() {

        ArgumentCaptor<Exception> exceptionArgumentCaptor = ArgumentCaptor.forClass(Exception.class);

        doAnswer(invocation -> {throw new RuntimeException((Exception)invocation.getArgument(0));})
                .when(mockOutputWriter).outputException(exceptionArgumentCaptor.capture());

        return exceptionArgumentCaptor;
    }

    private static String readFile(String fileName) throws URISyntaxException, IOException {
        URL url = PostGithubWebhookEventHandlerTest.class.getClassLoader().getResource(fileName);
        if (url == null) {
            throw new FileNotFoundException(fileName + " was not found in the resources directory.");
        }
        Path path = Paths.get(url.toURI());
        return Files.lines(path).collect(Collectors.joining("\n"));
    }

}
