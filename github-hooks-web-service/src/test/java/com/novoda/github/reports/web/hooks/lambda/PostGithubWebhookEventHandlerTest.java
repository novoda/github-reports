package com.novoda.github.reports.web.hooks.lambda;

import com.amazonaws.util.StringInputStream;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.novoda.github.reports.web.hooks.handler.EventForwarder;
import com.novoda.github.reports.web.hooks.model.WebhookRequest;
import com.novoda.github.reports.web.hooks.secret.PayloadVerifier;
import com.ryanharter.auto.value.gson.AutoValueGsonTypeAdapterFactory;

import java.io.OutputStream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class PostGithubWebhookEventHandlerTest {

    private final String JSON_REQUEST = "{\"method\":\"POST\",\"body\":{\"action\":\"created\",\"issue\":{\"id\":170115015,\"number\":5," +
            "\"title\":\"NullPointerException\",\"user\":{\"login\":\"takecare\",\"id\":212528,\"type\":\"User\"},\"state\":\"open\"," +
            "\"comments\":2,\"created_at\":\"2016-08-09T08:58:23Z\",\"updated_at\":\"2016-08-10T14:45:27Z\",\"pull_request\":{" +
            "\"url\":\"https://api.github.com/repos/takecare/dump/pulls/5\"},\"body\":\"wut wut wut\"},\"comment\":{" +
            "\"id\":238889574,\"user\":{\"login\":\"takecare\",\"id\":212528,\"type\":\"User\"},\"created_at\":\"2016-08-10T14:45:27Z\"," +
            "\"updated_at\":\"2016-08-10T14:45:27Z\",\"body\":\"another one\"},\"repository\":{\"id\":63702872,\"name\":\"dump\"," +
            "\"full_name\":\"takecare/dump\",\"owner\":{\"login\":\"takecare\",\"id\":212528,\"type\":\"User\"},\"private\":false,\"" +
            "description\":\"Github testbed \",\"fork\":false,\"created_at\":\"2016-07-19T14:51:48Z\",\"updated_at\":" +
            "\"2016-07-19T14:51:48Z\",\"has_issues\":true},\"sender\":{\"login\":\"takecare\",\"id\":212528,\"type\":\"User\"}},\"headers\":{" +
            "\"content-type\":\"application/json\",\"Host\":\"***REMOVED***\"," +
            "\"User-Agent\":\"GitHub-Hookshot/c80b888\",\"X-Hub-Signature\":\"sha1=0245b9d1f8012444a121aeadc0acf6669a9d0c53\"}}";

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
    public void givenARequest_whenHandlingIt_thenWeCheckIfItsPayloadIsValid() throws Exception {
        WebhookRequest request = gson.fromJson(JSON_REQUEST, WebhookRequest.class);

        handler.handleRequest(new StringInputStream(JSON_REQUEST), mock(OutputStream.class), null);

        verify(mockPayloadVerifier).checkIfPayloadIsValid(request);
    }

}
