package com.novoda.github.reports.data.db;

import com.novoda.github.reports.data.DataLayerException;
import com.novoda.github.reports.data.model.User;
import com.novoda.github.reports.data.model.UserStats;

import java.math.BigInteger;
import java.sql.SQLException;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.tools.jdbc.MockResult;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Matchers;

import static com.novoda.github.reports.data.db.DatabaseHelper.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class DbUserDataLayerTest {

    private static final int ANY_USER_ID = 1337;
    private static final String ANY_USER_NAME = "Tim Riggins";
    private DbUserDataLayer dataLayer;
    private MockConnectionManager mockConnectionManager;
    private DSLContext context;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws SQLException {
        mockConnectionManager = MockConnectionManager.newInstance();
        context = mockConnectionManager.getNewDSLContext(mockConnectionManager.getNewConnection());
        dataLayer = DbUserDataLayer.newInstance(mockConnectionManager);
    }

    @Test
    public void givenUserName_whenConvertingRecordsToUserStats_thenMustContainUserName() {
        String userName = "frapontillo";

        UserStats stats = dataLayer.recordsToUserStats(null, null, null, userName);

        assertEquals(stats.getUserName(), userName);
    }

    @Test
    public void givenNullResults_whenConvertingRecordsToUserStats_thenReturnsZeroedStats() {
        UserStats stats = dataLayer.recordsToUserStats(null, null, null, "frapontillo");

        verifyZeroedStats(stats);
    }

    @Test
    public void givenEmptyResults_whenConvertingRecordsToUserStats_thenReturnsZeroedStats() {
        Result<Record2<Integer, Integer>> events = givenEmptyEvents();
        Result<Record1<Integer>> peopleComments = givenEmptyPeopleComments();
        Result<Record1<Integer>> repositories = givenEmptyRepositories();

        UserStats stats = dataLayer.recordsToUserStats(events, peopleComments, repositories, "frapontillo");

        verifyZeroedStats(stats);
    }

    private void verifyZeroedStats(UserStats stats) {
        assertEquals(stats.getEventStats().getNumberOfOpenedIssues(), BigInteger.ZERO);
        assertEquals(stats.getEventStats().getNumberOfOpenedPullRequests(), BigInteger.ZERO);
        assertEquals(stats.getEventStats().getNumberOfCommentedIssues(), BigInteger.ZERO);
        assertEquals(stats.getEventStats().getNumberOfMergedPullRequests(), BigInteger.ZERO);
        assertEquals(stats.getEventStats().getNumberOfOtherEvents(), BigInteger.ZERO);
        assertEquals(stats.getNumberOfOtherPeopleComments(), BigInteger.ZERO);
        assertEquals(stats.getNumberOfRepositoriesWorkedOn(), BigInteger.ZERO);
    }

    @Test
    public void givenValidPeopleCommentsCount_whenConvertingRecordsToUserStats_thenReturnsProperStats() {
        Result<Record1<Integer>> peopleComments = givenEmptyPeopleComments();
        peopleComments.add(buildNewPeopleCommentsRecord(13));

        UserStats stats = dataLayer.recordsToUserStats(null, peopleComments, null, "frapontillo");

        assertEquals(stats.getNumberOfOtherPeopleComments(), BigInteger.valueOf(13));
    }

    @Test
    public void givenMultiplePeopleCommentsCount_whenConvertingRecordsToUserStats_thenIgnoreNonFirstPeopleCommentsCount() {
        Result<Record1<Integer>> peopleComments = givenEmptyPeopleComments();
        peopleComments.add(buildNewPeopleCommentsRecord(13));
        peopleComments.add(buildNewPeopleCommentsRecord(4));

        UserStats stats = dataLayer.recordsToUserStats(null, peopleComments, null, "frapontillo");

        assertEquals(stats.getNumberOfOtherPeopleComments(), BigInteger.valueOf(13));
    }

    @Test
    public void givenValidRepositoriesCount_whenConvertingRecordsToUserStats_thenReturnsProperStats() {
        Result<Record1<Integer>> repositories = givenEmptyRepositories();
        repositories.add(buildNewRepositoriesRecord(13));

        UserStats stats = dataLayer.recordsToUserStats(null, null, repositories, "frapontillo");

        assertEquals(stats.getNumberOfRepositoriesWorkedOn(), BigInteger.valueOf(13));
    }

    @Test
    public void givenMultipleRepositoriesCount_whenConvertingRecordsToUserStats_thenIgnoreNonFirstRepositoriesCount() {
        Result<Record1<Integer>> repositories = givenEmptyRepositories();
        repositories.add(buildNewRepositoriesRecord(13));
        repositories.add(buildNewRepositoriesRecord(4));

        UserStats stats = dataLayer.recordsToUserStats(null, null, repositories, "frapontillo");

        assertEquals(stats.getNumberOfRepositoriesWorkedOn(), BigInteger.valueOf(13));
    }

    private Result<Record2<Integer, Integer>> givenEmptyEvents() {
        return context.newResult(SELECT_EVENT_TYPE, SELECT_EVENTS_COUNT);
    }

    private Result<Record1<Integer>> givenEmptyPeopleComments() {
        return context.newResult(SELECT_EVENTS_COUNT);
    }

    private Result<Record1<Integer>> givenEmptyRepositories() {
        return context.newResult(SELECT_REPOSITORIES_COUNT);
    }

    private Record1<Integer> buildNewPeopleCommentsRecord(Integer peopleComments) {
        Record1<Integer> record = context.newRecord(SELECT_EVENTS_COUNT);
        record.set(SELECT_EVENTS_COUNT, peopleComments);
        return record;
    }

    private Record1<Integer> buildNewRepositoriesRecord(Integer repositories) {
        Record1<Integer> record = context.newRecord(SELECT_REPOSITORIES_COUNT);
        record.set(SELECT_REPOSITORIES_COUNT, repositories);
        return record;
    }

    @Test
    public void givenNewUser_whenUpdateOrInsertUser_thenReturnGivenUser() throws SQLException {
        User expectedUser = User.create(ANY_USER_ID, ANY_USER_NAME);
        whenUpdateOrInsertUserAffectsRows(1);

        User actualUser = null;
        try {
            actualUser = dataLayer.updateOrInsert(expectedUser);
        } catch (DataLayerException e) {
            fail();
        }

        assertEquals(expectedUser, actualUser);
    }

    @Test
    public void givenInvalidDatabase_whenUpdateOrInsertUser_thenThrowDataLayerException() throws SQLException, DataLayerException {
        User timRigginsUser = User.create(ANY_USER_ID, ANY_USER_NAME);
        whenUpdateOrInsertUserAffectsRows(2);

        thrown.expect(DataLayerException.class);
        thrown.expectMessage(Matchers.contains("More than"));
        dataLayer.updateOrInsert(timRigginsUser);
    }

    @Test
    public void givenValidUser_whenUpdateOrInsertUser_thenThrowDataLayerException() throws SQLException, DataLayerException {
        User timRigginsUser = User.create(ANY_USER_ID, ANY_USER_NAME);
        whenUpdateOrInsertUserAffectsRows(0);

        thrown.expect(DataLayerException.class);
        thrown.expectMessage(Matchers.contains("Could not"));
        dataLayer.updateOrInsert(timRigginsUser);
    }

    private void whenUpdateOrInsertUserAffectsRows(int numberOfAffectedRows) throws SQLException {
        when(mockConnectionManager.getMockDataProvider().execute(any()))
                .thenReturn(new MockResult[]{new MockResult(numberOfAffectedRows, null)});
    }
}
