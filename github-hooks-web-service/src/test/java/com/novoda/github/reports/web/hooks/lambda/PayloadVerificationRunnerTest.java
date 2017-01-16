package com.novoda.github.reports.web.hooks.lambda;

import com.google.gson.JsonObject;
import com.novoda.github.reports.web.hooks.model.WebhookRequest;
import com.novoda.github.reports.web.hooks.secret.InvalidSecretException;
import com.novoda.github.reports.web.hooks.secret.PayloadVerifier;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class PayloadVerificationRunnerTest {

    @Mock
    private PayloadVerifier mockPayloadVerifier;

    @Mock
    private OutputWriter mockOutputWriter;

    @InjectMocks
    private PayloadVerificationRunner mockPayloadVerificationRunner;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void givenValidRequest_whenRunningValidation_thenDelegateToPayloadVerifier() throws Exception {
        WebhookRequest request = aWebhookRequest();

        mockPayloadVerificationRunner.verify(request);

        verify(mockPayloadVerifier).checkIfPayloadIsValid(request);
    }

    @Test
    public void givenValidRequest_whenRunningValidation_thenNothingHappens() throws Exception {
        WebhookRequest request = aWebhookRequest();

        mockPayloadVerificationRunner.verify(request);

        verifyZeroInteractions(mockOutputWriter);
    }

    @Test
    public void givenInvalidRequest_whenRunningValidation_thenAnExceptionIsOutput() throws Exception {
        WebhookRequest request = aWebhookRequest();
        doThrow(InvalidSecretException.class).when(mockPayloadVerifier).checkIfPayloadIsValid(request);

        mockPayloadVerificationRunner.verify(request);

        verify(mockOutputWriter).outputException(any(InvalidSecretException.class));
    }

    private WebhookRequest aWebhookRequest() {
        return WebhookRequest.builder()
                .body(new JsonObject())
                .headers(new HashMap<>())
                .build();
    }
}
