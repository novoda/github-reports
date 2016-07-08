package com.novoda.github.reports.data.db;

import com.novoda.github.reports.data.model.EventStats;
import com.novoda.github.reports.data.model.ProjectRepoStats;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Result;
import org.junit.Before;
import org.junit.Test;

import static com.novoda.github.reports.data.db.DatabaseHelper.*;
import static org.junit.Assert.assertEquals;

public class DbProjectRepoDataLayerTest {

    private DSLContext context;

    @Before
    public void setUp() throws SQLException {
        ConnectionManager connectionManager = MockConnectionManager.newInstance();
        Connection connection = connectionManager.getNewConnection();
        context = connectionManager.getNewDSLContext(connection);
    }

    @Test
    public void givenProjectOrRepoName_whenConvertingRecordsToProjectRepoStats_thenMustContainProjectOrRepoName() {
        String projectOrRepoName = "repo";

        ProjectRepoStats stats = DatabaseHelper.recordsToProjectRepoStats(null, null, projectOrRepoName);

        assertEquals(stats.getProjectRepoName(), projectOrRepoName);
    }

    @Test
    public void givenNullResults_whenConvertingRecordsToProjectRepoStats_thenReturnsZeroedStats() {
        ProjectRepoStats stats = DatabaseHelper.recordsToProjectRepoStats(null, null, "repo");

        verifyZeroedStats(stats);
    }

    @Test
    public void givenEmptyResults_whenConvertingRecordsToProjectRepoStats_thenReturnsZeroedStats() {
        Result<Record2<Integer, Integer>> events = givenEmptyEvents();
        Result<Record1<Integer>> people = givenEmptyPeople();

        ProjectRepoStats stats = DatabaseHelper.recordsToProjectRepoStats(events, people, "repo");

        verifyZeroedStats(stats);
    }

    private void verifyZeroedStats(ProjectRepoStats stats) {
        assertEquals(stats.getEventStats().getNumberOfOpenedIssues(), BigInteger.ZERO);
        assertEquals(stats.getEventStats().getNumberOfOpenedPullRequests(), BigInteger.ZERO);
        assertEquals(stats.getEventStats().getNumberOfCommentedIssues(), BigInteger.ZERO);
        assertEquals(stats.getEventStats().getNumberOfMergedPullRequests(), BigInteger.ZERO);
        assertEquals(stats.getEventStats().getNumberOfOtherEvents(), BigInteger.ZERO);
        assertEquals(stats.getNumberOfParticipatingUsers(), BigInteger.ZERO);
    }

    @Test
    public void givenHandledEvents_whenConvertingRecordsToEventStats_thenReturnsProperStats() {
        Result<Record2<Integer, Integer>> events = givenEmptyEvents();
        Record2<Integer, Integer> openedIssuesRecord = buildNewEventRecord(OPENED_ISSUES_ID, 1);
        Record2<Integer, Integer> openedPrsRecord = buildNewEventRecord(OPENED_PULL_REQUESTS_ID, 2);
        Record2<Integer, Integer> commentedIssuesRecord = buildNewEventRecord(COMMENTED_ISSUES_ID, 4);
        Record2<Integer, Integer> commentedPrsRecord = buildNewEventRecord(COMMENTED_PULL_REQUESTS_ID, 5);
        Record2<Integer, Integer> mergedPrsRecord = buildNewEventRecord(MERGED_PULL_REQUESTS_ID, 7);
        events.addAll(Arrays.asList(
                openedIssuesRecord,
                openedPrsRecord,
                commentedIssuesRecord,
                commentedPrsRecord,
                mergedPrsRecord
        ));

        EventStats stats = DatabaseHelper.recordsToEventStats(events);

        assertEquals(stats.getNumberOfOpenedIssues(), BigInteger.valueOf(1));
        assertEquals(stats.getNumberOfOpenedPullRequests(), BigInteger.valueOf(2));
        assertEquals(stats.getNumberOfCommentedIssues(), BigInteger.valueOf(9));
        assertEquals(stats.getNumberOfMergedPullRequests(), BigInteger.valueOf(7));
    }

    @Test
    public void givenUnhandledEvents_whenConvertingRecordsToEventStats_thenReturnsProperStats() {
        Result<Record2<Integer, Integer>> events = givenEmptyEvents();
        Record2<Integer, Integer> anotherRecord = buildNewEventRecord(1337, 99);
        Record2<Integer, Integer> yetAnotherRecord = buildNewEventRecord(1337, 100);
        events.addAll(Arrays.asList(
                anotherRecord,
                yetAnotherRecord
        ));

        EventStats stats = DatabaseHelper.recordsToEventStats(events);

        assertEquals(stats.getNumberOfOtherEvents(), BigInteger.valueOf(199));
    }

    @Test
    public void givenValidPeopleCount_whenConvertingRecordsToProjectRepoStats_thenReturnsProperStats() {
        Result<Record1<Integer>> people = givenEmptyPeople();
        people.add(buildNewPeopleRecord(13));

        ProjectRepoStats stats = DatabaseHelper.recordsToProjectRepoStats(null, people, "repo");

        assertEquals(stats.getNumberOfParticipatingUsers(), BigInteger.valueOf(13));
    }

    @Test
    public void givenMultiplePeopleCount_whenConvertingRecordsToProjectRepoStats_thenIgnoreNonFirstPeopleCount() {
        Result<Record1<Integer>> people = givenEmptyPeople();
        people.add(buildNewPeopleRecord(13));
        people.add(buildNewPeopleRecord(4));

        ProjectRepoStats stats = DatabaseHelper.recordsToProjectRepoStats(null, people, "repo");

        assertEquals(stats.getNumberOfParticipatingUsers(), BigInteger.valueOf(13));
    }

    private Result<Record2<Integer, Integer>> givenEmptyEvents() {
        return context.newResult(SELECT_EVENT_TYPE, SELECT_EVENTS_COUNT);
    }

    private Result<Record1<Integer>> givenEmptyPeople() {
        return context.newResult(SELECT_PEOPLE_COUNT);
    }

    private Record2<Integer, Integer> buildNewEventRecord(Integer eventId, Integer eventsCount) {
        Record2<Integer, Integer> record = context.newRecord(SELECT_EVENT_TYPE, SELECT_EVENTS_COUNT);
        record.set(SELECT_EVENT_TYPE, eventId);
        record.set(SELECT_EVENTS_COUNT, eventsCount);
        return record;
    }

    private Record1<Integer> buildNewPeopleRecord(Integer people) {
        Record1<Integer> record = context.newRecord(SELECT_PEOPLE_COUNT);
        record.set(SELECT_PEOPLE_COUNT, people);
        return record;
    }

}
