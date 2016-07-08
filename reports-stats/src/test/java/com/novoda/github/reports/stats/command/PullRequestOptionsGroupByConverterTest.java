package com.novoda.github.reports.stats.command;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PullRequestOptionsGroupByConverterTest {

    private PullRequestOptionsGroupByConverter converter;

    @Before
    public void setUp() {
        converter = new PullRequestOptionsGroupByConverter();
    }

    @Test
    public void givenLongGroupByName_whenConvertingFromString_thenReturnProperGroupBy() {
        String longName = "week";

        PullRequestOptionsGroupBy actual = converter.convert(longName);

        assertEquals(PullRequestOptionsGroupBy.WEEK, actual);
    }

    @Test
    public void givenShortGroupByName_whenConvertingFromString_thenReturnProperGroupBy() {
        String shortName = "w";

        PullRequestOptionsGroupBy actual = converter.convert(shortName);

        assertEquals(PullRequestOptionsGroupBy.WEEK, actual);
    }

    @Test
    public void givenNotAcceptedGroupByName_whenConvertingFromString_thenReturnNoneGroupBy() {
        String nonExistingName = "somethingsomethingdangerzone";

        PullRequestOptionsGroupBy actual = converter.convert(nonExistingName);

        assertEquals(PullRequestOptionsGroupBy.NONE, actual);
    }

    @Test
    public void givenNullGroupByName_whenConvertingFromString_thenReturnNoneGroupBy() {
        String nullName = null;

        PullRequestOptionsGroupBy actual = converter.convert(nullName);

        assertEquals(PullRequestOptionsGroupBy.NONE, actual);
    }

}
