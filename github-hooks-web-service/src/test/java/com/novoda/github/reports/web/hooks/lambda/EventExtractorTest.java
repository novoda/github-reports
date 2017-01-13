package com.novoda.github.reports.web.hooks.lambda;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;
import com.novoda.github.reports.web.hooks.model.WebhookRequest;
import com.ryanharter.auto.value.gson.AutoValueGsonTypeAdapterFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class EventExtractorTest {

    @Mock
    private OutputWriter mockOutputWriter;

    private Gson gson;

    private EventExtractor eventExtractor;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        gson = new GsonBuilder()
                .registerTypeAdapterFactory(new AutoValueGsonTypeAdapterFactory())
                .create();
        eventExtractor = new EventExtractor(mockOutputWriter, gson);
    }

    @Test
    public void givenAValidRequest_whenExtractingTheEvent_thenItIsReturned() throws Exception {
        WebhookRequest request = givenWebhookRequestFrom("valid_request.json");

        GithubWebhookEvent event = eventExtractor.extractFrom(request);

        assertThat(event).isEqualToComparingFieldByFieldRecursively(givenWebhookEventFrom("valid_request.json"));
    }

    @Test
    public void givenARequestMissingItsBody_whenExtractingTheEvent_thenAnExceptionIsOutput() throws Exception {
        WebhookRequest request = givenWebhookRequestFrom("no_body.json");

        eventExtractor.extractFrom(request);

        verify(mockOutputWriter).outputException(any(NullPointerException.class));
    }

    @Test
    public void givenAnInvalidRequest_whenExtractingTheEvent_thenAnExceptionIsOutput() throws Exception {
        WebhookRequest request = givenWebhookRequestFrom("invalid_action.json");

        eventExtractor.extractFrom(request);

        verify(mockOutputWriter).outputException(any(ArrayStoreException.class));
    }

    private WebhookRequest givenWebhookRequestFrom(String filename) {
        String json = "";
        try {
            json = readFile(filename);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        return gson.fromJson(json, WebhookRequest.class);
    }

    private GithubWebhookEvent givenWebhookEventFrom(String filename) {
        WebhookRequest request = givenWebhookRequestFrom(filename);
        return gson.fromJson(request.body(), GithubWebhookEvent.class);
    }

    private static String readFile(String fileName) throws URISyntaxException, IOException {
        URL url = EventExtractorTest.class.getClassLoader().getResource(fileName);
        if (url == null) {
            throw new FileNotFoundException(fileName + " was not found in the resources directory.");
        }
        Path path = Paths.get(url.toURI());
        return Files.lines(path).collect(Collectors.joining("\n"));
    }
}
