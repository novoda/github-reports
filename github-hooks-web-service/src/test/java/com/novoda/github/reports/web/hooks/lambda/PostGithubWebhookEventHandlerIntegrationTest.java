package com.novoda.github.reports.web.hooks.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.novoda.github.reports.web.hooks.classification.EventType;
import com.novoda.github.reports.web.hooks.classification.WebhookEventClassifier;
import com.novoda.github.reports.web.hooks.handler.EventForwarder;
import com.novoda.github.reports.web.hooks.handler.EventHandler;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;
import com.novoda.github.reports.web.hooks.model.WebhookRequest;
import com.novoda.github.reports.web.hooks.secret.PayloadVerifier;
import com.ryanharter.auto.value.gson.AutoValueGsonTypeAdapterFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class PostGithubWebhookEventHandlerIntegrationTest {

    private static final Context ANY_CONTEXT = null;

    @Mock
    private PayloadVerifier mockPayloadVerifier;

    @Mock
    private Logger mockLogger;

    @Mock
    private EventHandler mockEventHandler;

    @Mock
    private OutputStream outputStream;

    private Gson gson;

    private PostGithubWebhookEventHandler handler;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        gson = new GsonBuilder()
                .registerTypeAdapterFactory(new AutoValueGsonTypeAdapterFactory())
                .create();

        OutputWriter outputWriter = OutputWriter.newInstance(gson);

        PayloadVerificationRunner payloadVerificationRunner = new PayloadVerificationRunner(mockPayloadVerifier, outputWriter);

        InputStreamReaderFactory inputStreamReaderFactory = new InputStreamReaderFactory();
        WebhookRequestExtractor webhookRequestExtractor = new WebhookRequestExtractor(inputStreamReaderFactory, outputWriter, gson);

        EventExtractor eventExtractor = new EventExtractor(outputWriter, gson);

        WebhookEventClassifier eventClassifier = new WebhookEventClassifier();
        Map<EventType, EventHandler> handlers = new HashMap<>();

        Arrays.stream(EventType.values())
                .forEach(eventType -> handlers.put(eventType, mockEventHandler));

        EventForwarder eventForwarder = new EventForwarder(eventClassifier, handlers);

        handler = new PostGithubWebhookEventHandler(
                payloadVerificationRunner,
                webhookRequestExtractor,
                eventExtractor,
                eventForwarder,
                outputWriter,
                mockLogger
        );
    }

    @Test
    public void givenAValidRequest_whenHandlingIt_thenEventIsHandled() throws Exception {
        InputStream inputStream = inputStreamFrom("reopened_action.json");

        handler.handleRequest(inputStream, outputStream, ANY_CONTEXT);

        ArgumentCaptor<GithubWebhookEvent> eventCaptor = ArgumentCaptor.forClass(GithubWebhookEvent.class);
        verify(mockEventHandler).handle(eventCaptor.capture());

        GithubWebhookEvent expectedEvent = eventFrom(readFile("reopened_action.json"));

        assertThat(eventCaptor.getValue()).isEqualToComparingFieldByFieldRecursively(expectedEvent);
    }

    @Test(expected = RuntimeException.class)
    public void givenARequestWithAnInvalidAction_whenHandlingIt_thenAnExceptionIsThrown() throws Exception {
        InputStream inputStream = inputStreamFrom("invalid_action.json");

        handler.handleRequest(inputStream, outputStream, ANY_CONTEXT);

    }

    private GithubWebhookEvent eventFrom(String contents) {
        WebhookRequest request = gson.fromJson(contents, WebhookRequest.class);
        return gson.fromJson(request.body(), GithubWebhookEvent.class);
    }

    private static String readFile(String fileName) throws URISyntaxException, IOException {
        URL url = urlFor(fileName);
        Path path = Paths.get(url.toURI());
        return Files.lines(path).collect(Collectors.joining("\n"));
    }

    private static InputStream inputStreamFrom(String fileName) throws URISyntaxException, IOException {
        URL url = urlFor(fileName);
        File file = new File(url.toURI());
        return new FileInputStream(file);
    }

    private static URL urlFor(String fileName) throws FileNotFoundException {
        URL url = PostGithubWebhookEventHandlerTest.class.getClassLoader().getResource(fileName);
        if (url == null) {
            throw new FileNotFoundException(fileName + " was not found in the resources directory.");
        }
        return url;
    }

}
