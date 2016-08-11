package com.novoda.github.reports.web.hooks.secret;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class HashSignatureCreatorTest {

    private static String SECRET = "pessoa";

    @Mock
    private SecretPropertiesReader mockSecretPropertiesReader;

    @InjectMocks
    private HashSignatureCreator hashSignatureCreator;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        when(mockSecretPropertiesReader.getSecret()).thenReturn(SECRET);
    }

    @Test
    public void givenAPayload_whenCreatingASignatureForIt_thenItIsTheExpected() throws InvalidSecretException {
        String payload = "Deus quer, o homem sonha, a obra nasce";

        String actual = hashSignatureCreator.createSignatureFor(payload);

        assertEquals("sha1=5e1e041c4d02d681daf9d2337a2c9fcb8cd5588d", actual);
    }

}
