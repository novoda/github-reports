package com.novoda.github.reports.web.hooks.lambda;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.novoda.github.reports.web.hooks.model.WebhookRequest;
import com.ryanharter.auto.value.gson.AutoValueGsonTypeAdapterFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.MockitoAnnotations.initMocks;

public class WebhookRequestExtractorTest {

    @Mock
    private InputStreamReaderFactory inputStreamReaderFactory;

    @Mock
    private OutputWriter outputWriter;

    @Mock
    private InputStream inputStream;

    private Gson gson;

    private WebhookRequestExtractor webhookRequestExtractor;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        gson = new GsonBuilder()
                .registerTypeAdapterFactory(new AutoValueGsonTypeAdapterFactory())
                .create();
        webhookRequestExtractor = new WebhookRequestExtractor(inputStreamReaderFactory, outputWriter, gson);
    }

    @Test
    public void givenInputWithValidRequest_whenExtractingTheRequest_thenItIsReturned() throws Exception {
        given(inputStreamReaderFactory.createFor(inputStream)).willReturn(readFile("valid_request.json"));

        WebhookRequest request = webhookRequestExtractor.extractFrom(inputStream);

        assertThat(request).isEqualToComparingFieldByFieldRecursively(givenWebhookRequestFrom("valid_request.json"));
    }

    @Test(expected = RuntimeException.class)
    public void givenInputWithAnInvalidRequest_whenExtractingTheRequest_thenAnExceptionIsOutput() throws Exception {
        given(inputStreamReaderFactory.createFor(inputStream)).willReturn(readFile("invalid_request.json"));
        doThrow(RuntimeException.class).when(outputWriter).outputException(any(Exception.class));

        webhookRequestExtractor.extractFrom(inputStream);

    }

    private static InputStreamReader readFile(String fileName) throws URISyntaxException, IOException {
        URL url = WebhookRequestExtractorTest.class.getClassLoader().getResource(fileName);
        if (url == null) {
            throw new FileNotFoundException(fileName + " was not found in the resources directory.");
        }
        File file = new File(url.toURI());
        FileInputStream fileInputStream = new FileInputStream(file);
        return new InputStreamReader(fileInputStream);
    }

    private WebhookRequest givenWebhookRequestFrom(String filename) throws IOException, URISyntaxException {
        return gson.fromJson(readFile(filename), WebhookRequest.class);
    }
}
