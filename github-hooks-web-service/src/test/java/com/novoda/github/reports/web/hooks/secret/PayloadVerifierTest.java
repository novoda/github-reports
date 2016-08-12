package com.novoda.github.reports.web.hooks.secret;

import com.google.gson.JsonObject;
import com.novoda.github.reports.web.hooks.model.WebhookRequest;

import java.util.Collections;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PayloadVerifierTest {

    private static final String ANY_SIGNATURE = "assinatura";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private SecretSignatureExtractor mockSecretSignatureExtractor;

    @Mock
    private HashSignatureCreator mockHashSignatureCreator;

    @InjectMocks
    private PayloadVerifier payloadVerifier;

    private ArgumentCaptor<String> signatureCreatorCaptor = ArgumentCaptor.forClass(String.class);

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        when(mockHashSignatureCreator.createSignatureFor(anyString())).thenReturn(ANY_SIGNATURE);
        when(mockSecretSignatureExtractor.extractSignatureFrom(any(WebhookRequest.class))).thenReturn(ANY_SIGNATURE);
    }

    @Test
    public void givenAPayloadBody_whenCheckingIfItIsValid_thenItIsProperlyConvertedToJson() throws Exception {
        JsonObject body = givenABody();

        payloadVerifier.checkIfPayloadIsValid(givenARequest());

        verify(mockHashSignatureCreator).createSignatureFor(signatureCreatorCaptor.capture());
        assertEquals(body.toString(), signatureCreatorCaptor.getValue());
    }

    @Test(expected = InvalidSecretException.class)
    public void givenSignaturesDoNotMatch_whenCheckingIfItIsValid_thenAnExceptionIsThrown() throws Exception {
        when(mockHashSignatureCreator.createSignatureFor(anyString())).thenReturn("no match");

        payloadVerifier.checkIfPayloadIsValid(givenARequest());

    }

    private WebhookRequest givenARequest() {
        return WebhookRequest.builder()
                .body(givenABody())
                .headers(Collections.singletonMap("key", "value"))
                .build();
    }

    private JsonObject givenABody() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("prop","value");
        return jsonObject;
    }

}
