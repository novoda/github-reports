package com.novoda.github.reports.web.hooks.secret;

import com.novoda.github.reports.properties.PropertiesReader;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SecretPropertiesReaderTest {

    private static final java.lang.String ANY_KEY = "sempartirtudo";

    @Mock
    private PropertiesReader mockPropertiesReader;

    @InjectMocks
    private SecretPropertiesReader secretPropertiesReader;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        when(mockPropertiesReader.readProperty("SECRET")).thenReturn(ANY_KEY);
    }

    @Test
    public void givenAPropertiesReader_whenReadingTheSecret_thenItIsReturned() {

        String actual = secretPropertiesReader.getSecret();

        assertEquals(ANY_KEY, actual);
    }
}
