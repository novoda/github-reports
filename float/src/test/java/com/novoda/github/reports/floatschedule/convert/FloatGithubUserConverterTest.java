package com.novoda.github.reports.floatschedule.convert;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FloatGithubUserConverterTest {

    private Map<String, String> mapFromReader;

    @Mock
    JsonMapReader<Map<String, String>> mockJsonMapReader;

    private FloatGithubUserConverter floatGithubUserConverter;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        floatGithubUserConverter = new FloatGithubUserConverter(mockJsonMapReader);

        mapFromReader = new HashMap<>(1);
        mapFromReader.put("Float Pirata", "github meirinho");
        when(mockJsonMapReader.readFromResource("users.json")).thenReturn(mapFromReader);
    }

    @Test
    public void givenAJsonFileWithUsers_whenGettingTheFloatUsernameForAGithubUsername_thenItIsTheExpected() throws Exception {

        String actual = floatGithubUserConverter.getFloatUser("github meirinho");

        assertEqualsIgnoreCase("float pirata", actual);
    }

    private void assertEqualsIgnoreCase(String expected, String actual) {
        assertTrue(expected.equalsIgnoreCase(actual));
    }

    @Test
    public void givenAJsonFileWithUsers_whenGettingTheGithubUsernameForAFloatUsername_thenItIsTheExpeced() throws Exception {

        String actual = floatGithubUserConverter.getGithubUser("float pirata");

        assertEqualsIgnoreCase("github meirinho", actual);
    }
}
