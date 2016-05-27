package com.novoda.github.reports.batch.properties;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PropertiesReaderTest {

    private static final String PROPERTY_FILE_PATH = PropertiesReaderTest.class.getClassLoader().getResource("test.properties").getFile();
    private static final String PROPERTY_TEST_NAME = "test";
    private static final String PROPERTY_TEST_VALUE = "property";
    private static final String PROPERTY_EMPTY_NAME = "empty";
    private static final String EMPTY_STRING = "";

    private PropertiesReader propertiesReader;

    @Before
    public void setUp() {
        propertiesReader = PropertiesReader.newInstance(PROPERTY_FILE_PATH);
    }

    @Test
    public void givenValidFileNameAndProperty_whenGetProperty_thenReturnCorrectValue() {
        String prop = propertiesReader.readProperty(PROPERTY_TEST_NAME);

        assertEquals(prop, PROPERTY_TEST_VALUE);
    }

    @Test
    public void givenValidFileNameAndEmptyProperty_whenGetProperty_thenReturnEmptyString() {
        String prop = propertiesReader.readProperty(PROPERTY_EMPTY_NAME);

        assertEquals(prop, EMPTY_STRING);
    }

}
