package com.novoda.github.reports.service.network;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EpochTimeConverterTest {

    private static final int ANY_EPOCH_TIMESTAMP = 1464692786;

    private EpochTimeConverter epochTimeConverter;

    @Before
    public void setUp() {
        epochTimeConverter = new EpochTimeConverter();
    }

    @Test
    public void conversionFromSecondsToMillisConvertsProperly() {

        long actual = epochTimeConverter.toMillis(ANY_EPOCH_TIMESTAMP);
        long expected = ANY_EPOCH_TIMESTAMP * 1000L;

        assertEquals(expected, actual);
    }

    @Test
    public void conversionFromMillisToSecondsConvertsProperly() {

        long actual = epochTimeConverter.toSeconds(ANY_EPOCH_TIMESTAMP);
        long expected = ANY_EPOCH_TIMESTAMP / 1000L;

        assertEquals(expected, actual);
    }

}
