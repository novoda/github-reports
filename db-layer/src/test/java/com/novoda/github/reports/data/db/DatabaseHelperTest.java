package com.novoda.github.reports.data.db;

import java.sql.Timestamp;
import java.util.GregorianCalendar;

import org.jooq.Condition;
import org.junit.Test;

import static com.novoda.github.reports.data.db.tables.Event.EVENT;
import static org.junit.Assert.assertEquals;

public class DatabaseHelperTest {

    @Test
    public void givenFullRange_whenConditionalBetween_thenReturnsFullRangeCondition() {
        Timestamp from = new Timestamp(new GregorianCalendar(2015, 1, 1).getTime().getTime());
        Timestamp to = new Timestamp(new GregorianCalendar(2015, 11, 31).getTime().getTime());

        Condition actualCondition = DatabaseHelper.conditionalBetween(EVENT.DATE, from, to);

        assertEquals(actualCondition.toString(), "(\n" +
                "  \"reports\".\"event\".\"date\" is not null\n" +
                "  and \"reports\".\"event\".\"date\" >= timestamp '2015-02-01 00:00:00.0'\n" +
                "  and \"reports\".\"event\".\"date\" <= timestamp '2015-12-31 00:00:00.0'\n" +
                ")");
    }

    @Test
    public void givenLeftRange_whenConditionalBetween_thenReturnsLeftRangeCondition() {
        Timestamp from = new Timestamp(new GregorianCalendar(2015, 1, 1).getTime().getTime());

        Condition actualCondition = DatabaseHelper.conditionalBetween(EVENT.DATE, from, null);

        assertEquals(actualCondition.toString(), "(\n" +
                "  \"reports\".\"event\".\"date\" is not null\n" +
                "  and \"reports\".\"event\".\"date\" >= timestamp '2015-02-01 00:00:00.0'\n" +
                ")");
    }

    @Test
    public void givenRightRange_whenConditionalBetween_thenReturnsRightRangeCondition() {
        Timestamp to = new Timestamp(new GregorianCalendar(2015, 11, 31).getTime().getTime());

        Condition actualCondition = DatabaseHelper.conditionalBetween(EVENT.DATE, null, to);

        assertEquals(actualCondition.toString(), "(\n" +
                "  \"reports\".\"event\".\"date\" is not null\n" +
                "  and \"reports\".\"event\".\"date\" <= timestamp '2015-12-31 00:00:00.0'\n" +
                ")");
    }

    @Test
    public void givenNoRange_whenConditionalBetween_thenReturnsSimpleNotNullCondition() {
        Condition actualCondition = DatabaseHelper.conditionalBetween(EVENT.DATE, null, null);

        assertEquals(actualCondition.toString(), "\"reports\".\"event\".\"date\" is not null");
    }

}
