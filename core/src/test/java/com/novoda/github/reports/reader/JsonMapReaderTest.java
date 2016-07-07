package com.novoda.github.reports.reader;

import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JsonMapReaderTest {

    private JsonMapReader<Map<String, String>> stringToStringReader;
    private JsonMapReader<Map<String, List<String>>> stringToStringsReader;

    @Before
    public void setUp() {
        stringToStringReader = JsonMapReader.newStringToStringInstance();
        stringToStringsReader = JsonMapReader.newStringToListOfStringsInstance();
    }

    @Test(expected = FileNotFoundException.class)
    public void givenANonExistentFile_whenReadingIt_thenAnExceptionIsThrown() throws Exception {

        stringToStringReader.readFromResource("nope.json");
    }

    @Test
    public void givenAJsonFile_whenReadingItAsAMapOfStringToString_thenItContainsAnExpectedKey() throws Exception {

        Map<String, String> map = stringToStringReader.readFromResource("stringToString.json");

        assertTrue(map.containsKey("key0"));
    }

    @Test
    public void givenAJsonFile_whenReadingItAsAMapOfStringToString_thenItContainsAnExpectedKeyMappedToTheRightValue() throws Exception {

        Map<String, String> actual = stringToStringReader.readFromResource("stringToString.json");

        assertEquals("value1", actual.get("key1"));
    }

    @Test
    public void givenAJsonFile_whenReadingItAsAMapOfStringToStrings_thenItContainsAnExpectedKey() throws Exception {

        Map<String, String> actual = stringToStringReader.readFromResource("stringToStrings.json");

        assertTrue(actual.containsKey("key1"));
    }

    @Test
    public void givenAJsonFile_whenReadingItAsAMapOfStringToStrings_thenItContainsAnExpectedKeyMappedToTheRightValue() throws Exception {

        Map<String, List<String>> actual = stringToStringsReader.readFromResource("stringToStrings.json");

        List<String> expected = Arrays.asList("value2a", "value2b", "value2c");
        assertEquals(expected, actual.get("key2"));
    }
}
