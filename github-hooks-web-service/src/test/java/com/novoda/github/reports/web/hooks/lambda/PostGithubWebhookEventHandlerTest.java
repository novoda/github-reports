package com.novoda.github.reports.web.hooks.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.util.StringInputStream;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.novoda.github.reports.web.hooks.handler.EventForwarder;
import com.novoda.github.reports.web.hooks.handler.UnhandledEventException;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;
import com.novoda.github.reports.web.hooks.model.WebhookRequest;
import com.novoda.github.reports.web.hooks.secret.InvalidSecretException;
import com.novoda.github.reports.web.hooks.secret.PayloadVerifier;
import com.ryanharter.auto.value.gson.AutoValueGsonTypeAdapterFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class PostGithubWebhookEventHandlerTest {

    private static final Context ANY_CONTEXT = null;

    @Mock
    private PayloadVerifier mockPayloadVerifier;

    @Mock
    private EventForwarder mockEventForwarder;

    @Mock
    private OutputWriter mockOutputWriter;

    @Mock
    private Logger mockLogger;

    private Gson gson;

    private PostGithubWebhookEventHandler handler;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        gson = new GsonBuilder()
                .registerTypeAdapterFactory(new AutoValueGsonTypeAdapterFactory())
                .create();

        handler = new PostGithubWebhookEventHandler(mockPayloadVerifier, mockEventForwarder, mockOutputWriter, mockLogger, gson);
    }

    @Test
    public void givenAValidRequest_whenHandlingIt_thenWeCheckIfItsPayloadIsValid() throws Exception {
        WebhookRequest request = gson.fromJson(givenAValidJsonRequest(), WebhookRequest.class);

        handler.handleRequest(new StringInputStream(givenAValidJsonRequest()), mock(OutputStream.class), ANY_CONTEXT);

        verify(mockPayloadVerifier).checkIfPayloadIsValid(request);
    }

    @Test
    public void givenAValidRequest_whenHandlingIt_thenTheEventIsForwarded() throws Exception {
        WebhookRequest request = gson.fromJson(givenAValidJsonRequest(), WebhookRequest.class);

        handler.handleRequest(new StringInputStream(givenAValidJsonRequest()), mock(OutputStream.class), ANY_CONTEXT);

        assertThatTheEventIsForwarded(request);
    }

    @Test(expected = RuntimeException.class)
    public void givenARequestWithInvalidSignature_whenHandlingIt_thenAnExceptionIsThrown() throws Exception {
        String json = readFile("invalid_signature_request.json");
        givenPayloadVerificationFails();

        handler.handleRequest(new StringInputStream(json), mock(OutputStream.class), ANY_CONTEXT);

    }

    private void givenPayloadVerificationFails() throws InvalidSecretException {
        doThrow(RuntimeException.class).when(mockOutputWriter).outputException(any(Exception.class));
        doThrow(InvalidSecretException.class).when(mockPayloadVerifier).checkIfPayloadIsValid(any(WebhookRequest.class));
    }

    @Test(expected = RuntimeException.class)
    public void givenAnInvalidAction_whenHandlingIt_thenAnExceptionIsThrown() throws Exception {
        String json = readFile("invalid_action.json");
        doThrow(RuntimeException.class).when(mockOutputWriter).outputException(any(Exception.class));

        handler.handleRequest(new StringInputStream(json), mock(OutputStream.class), ANY_CONTEXT);

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

    private String givenAValidJsonRequest() throws IOException, URISyntaxException {
        return readFile("valid_request.json");
    }

    private void assertThatTheEventIsForwarded(WebhookRequest request) throws UnhandledEventException {
        ArgumentCaptor<GithubWebhookEvent> argumentCaptor = ArgumentCaptor.forClass(GithubWebhookEvent.class);
        verify(mockEventForwarder).forward(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isEqualToComparingFieldByFieldRecursively(getEventFrom(request));
    }

    private GithubWebhookEvent getEventFrom(WebhookRequest request) {
        return gson.fromJson(request.body(), GithubWebhookEvent.class);
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
