package com.novoda.github.reports.web.hooks.secret;

import com.google.gson.JsonObject;
import com.novoda.github.reports.service.GithubUser;
import com.novoda.github.reports.web.hooks.model.GithubAction;
import com.novoda.github.reports.web.hooks.model.WebhookRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;

public class SecretSignatureExtractorTest {

    private static final String ANY_SECRET_KEY = "s3c®37k3y";
    private static final GithubAction ANY_ACTION = GithubAction.OPENED;
    private static final GithubUser ANY_GITHUB_USER = new GithubUser(88L);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @InjectMocks
    private SecretSignatureExtractor secretSignatureExtractor;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void givenARequestWithSignatureHeader_whenExtractSignature_thenSignatureIsExtracted() throws SecretException {
        WebhookRequest request = givenARequestWithHeaders(Collections.singletonMap("X-Hub-Signature", ANY_SECRET_KEY));

        String actual = secretSignatureExtractor.extractSignatureFrom(request);

        assertEquals(ANY_SECRET_KEY, actual);
    }

    private WebhookRequest givenARequestWithHeaders(Map<String, String> headers) {
        return WebhookRequest.builder()
                .headers(headers)
                .body(new JsonObject())
                .build();
    }

    @Test
    public void givenARequestWithoutSignatureHeader_whenExtractSignature_thenExceptionIsThrown() throws SecretException {
        expectedException.expect(SecretException.class);
        WebhookRequest request = givenARequestWithHeaders(new HashMap<>(0));

        secretSignatureExtractor.extractSignatureFrom(request);
    }
}
