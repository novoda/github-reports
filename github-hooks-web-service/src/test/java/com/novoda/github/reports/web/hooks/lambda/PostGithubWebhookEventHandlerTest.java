package com.novoda.github.reports.web.hooks.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.util.StringInputStream;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.novoda.github.reports.web.hooks.handler.EventForwarder;
import com.novoda.github.reports.web.hooks.handler.UnhandledEventException;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;
import com.novoda.github.reports.web.hooks.model.WebhookRequest;
import com.novoda.github.reports.web.hooks.secret.PayloadVerifier;
import com.novoda.github.reports.web.hooks.secret.SecretException;
import com.ryanharter.auto.value.gson.AutoValueGsonTypeAdapterFactory;

import java.io.OutputStream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class PostGithubWebhookEventHandlerTest {

    private static final Context ANY_CONTEXT = null;

    private static final String VALID_KEY = "0245b9d1f8012444a121aeadc0acf6669a9d0c53";
    private static final String INVALID_KEY = "0245b9d1f22444a121aeaoic0acf6669a9d0c53";
    private static final String JSON_REQUEST = "{\"method\":\"POST\",\"body\":{\"action\":\"created\",\"issue\":{\"id\":170115015," +
            "\"number\":5,\"title\":\"NullPointerException\",\"user\":{\"login\":\"takecare\",\"id\":212528,\"type\":\"User\"}," +
            "\"state\":\"open\",\"comments\":2,\"created_at\":\"2016-08-09T08:58:23Z\",\"updated_at\":\"2016-08-10T14:45:27Z\"," +
            "\"pull_request\":{\"url\":\"https://api.github.com/repos/takecare/dump/pulls/5\"},\"body\":\"wut wut wut\"},\"comment\":{" +
            "\"id\":238889574,\"user\":{\"login\":\"takecare\",\"id\":212528,\"type\":\"User\"},\"created_at\":\"2016-08-10T14:45:27Z\"," +
            "\"updated_at\":\"2016-08-10T14:45:27Z\",\"body\":\"another one\"},\"repository\":{\"id\":63702872,\"name\":\"dump\"," +
            "\"full_name\":\"takecare/dump\",\"owner\":{\"login\":\"takecare\",\"id\":212528,\"type\":\"User\"},\"private\":false,\"" +
            "description\":\"Github testbed \",\"fork\":false,\"created_at\":\"2016-07-19T14:51:48Z\",\"updated_at\":" +
            "\"2016-07-19T14:51:48Z\",\"has_issues\":true},\"sender\":{\"login\":\"takecare\",\"id\":212528,\"type\":\"User\"}},\"headers\":{" +
            "\"content-type\":\"application/json\",\"Host\":\"***REMOVED***\"," +
            "\"User-Agent\":\"GitHub-Hookshot/c80b888\",\"X-Hub-Signature\":\"sha1=%s\"}}";
    private static final String VALID_JSON_REQUEST = String.format(JSON_REQUEST, VALID_KEY);
    private static final String INVALID_JSON_REQUEST = String.format(JSON_REQUEST, INVALID_KEY);

    @Mock
    private PayloadVerifier mockPayloadVerifier;

    @Mock
    private EventForwarder mockEventForwarder;

    @Mock
    private OutputWriter mockOutputWriter;

    @Mock
    private Logger mockLogger;

    @InjectMocks
    private PostGithubWebhookEventHandler handler;

    private Gson gson;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        gson = new GsonBuilder()
                .registerTypeAdapterFactory(new AutoValueGsonTypeAdapterFactory())
                .create();
    }

    @Test
    public void givenAValidRequest_whenHandlingIt_thenWeCheckIfItsPayloadIsValid() throws Exception {
        WebhookRequest request = gson.fromJson(VALID_JSON_REQUEST, WebhookRequest.class);

        handler.handleRequest(new StringInputStream(VALID_JSON_REQUEST), mock(OutputStream.class), ANY_CONTEXT);

        verify(mockPayloadVerifier).checkIfPayloadIsValid(request);
    }

    @Test
    public void givenAValidRequest_whenHandlingIt_thenTheEventIsForwarded() throws Exception {
        WebhookRequest request = gson.fromJson(VALID_JSON_REQUEST, WebhookRequest.class);

        handler.handleRequest(new StringInputStream(VALID_JSON_REQUEST), mock(OutputStream.class), ANY_CONTEXT);

        assertThatTheEventIsForwarded(request);
    }

    private void assertThatTheEventIsForwarded(WebhookRequest request) throws UnhandledEventException {
        ArgumentCaptor<GithubWebhookEvent> argumentCaptor = ArgumentCaptor.forClass(GithubWebhookEvent.class);
        verify(mockEventForwarder).forward(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isEqualToComparingFieldByFieldRecursively(getEventFrom(request));
    }

    private GithubWebhookEvent getEventFrom(WebhookRequest request) {
        return gson.fromJson(request.body(), GithubWebhookEvent.class);
    }

    @Test(expected = RuntimeException.class)
    public void givenAnInvalidRequest_whenHandlingIt_thenAnExceptionIsThrown() throws Exception {
        doThrow(SecretException.class).when(mockPayloadVerifier).checkIfPayloadIsValid(any(WebhookRequest.class));

        handler.handleRequest(new StringInputStream(INVALID_JSON_REQUEST), mock(OutputStream.class), ANY_CONTEXT);

    }

}
