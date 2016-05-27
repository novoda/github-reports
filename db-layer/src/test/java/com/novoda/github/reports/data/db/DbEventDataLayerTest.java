package com.novoda.github.reports.data.db;

import com.novoda.github.reports.data.DataLayerException;
import com.novoda.github.reports.data.model.Event;
import com.novoda.github.reports.data.model.EventType;

import java.sql.SQLException;
import java.util.Date;
import java.util.GregorianCalendar;

import org.jooq.tools.jdbc.MockResult;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Matchers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class DbEventDataLayerTest {

    private static final int ANY_EVENT_ID = 1337;
    private static final Integer ANY_EVENT_REPO_ID = 1;
    private static final Integer ANY_EVENT_AUTHOR_ID = 2;
    private static final Integer ANY_EVENT_OWNER_ID = 3;
    private static final EventType ANY_EVENT_TYPE = EventType.ISSUE_COMMENT_ADD;
    private static final Date ANY_EVENT_DATE = new GregorianCalendar(2016, 4, 26, 18, 20).getTime();
    private DbEventDataLayer dataLayer;
    private MockConnectionManager mockConnectionManager;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws SQLException {
        mockConnectionManager = MockConnectionManager.newInstance();
        dataLayer = DbEventDataLayer.newInstance(mockConnectionManager);
    }

    @Test
    public void givenNewEvent_whenUpdateOrInsertEvent_thenReturnGivenEvent() throws SQLException {
        Event expectedEvent = Event.create(ANY_EVENT_ID, ANY_EVENT_REPO_ID, ANY_EVENT_AUTHOR_ID, ANY_EVENT_OWNER_ID, ANY_EVENT_TYPE, ANY_EVENT_DATE);
        whenUpdateOrInsertEventAffectsRows(1);

        Event actualEvent = null;
        try {
            actualEvent = dataLayer.updateOrInsert(expectedEvent);
        } catch (DataLayerException e) {
            fail();
        }

        assertEquals(expectedEvent, actualEvent);
    }

    @Test
    public void givenInvalidDatabase_whenUpdateOrInsertEvent_thenThrowDataLayerException() throws SQLException, DataLayerException {
        Event awesomeEvent = Event.create(ANY_EVENT_ID, ANY_EVENT_REPO_ID, ANY_EVENT_AUTHOR_ID, ANY_EVENT_OWNER_ID, ANY_EVENT_TYPE, ANY_EVENT_DATE);
        whenUpdateOrInsertEventAffectsRows(2);

        thrown.expect(DataLayerException.class);
        thrown.expectMessage(Matchers.contains("More than"));
        dataLayer.updateOrInsert(awesomeEvent);
    }

    @Test
    public void givenValidEvent_whenUpdateOrInsertEvent_thenThrowDataLayerException() throws SQLException, DataLayerException {
        Event awesomeEvent = Event.create(ANY_EVENT_ID, ANY_EVENT_REPO_ID, ANY_EVENT_AUTHOR_ID, ANY_EVENT_OWNER_ID, ANY_EVENT_TYPE, ANY_EVENT_DATE);
        whenUpdateOrInsertEventAffectsRows(0);

        thrown.expect(DataLayerException.class);
        thrown.expectMessage(Matchers.contains("Could not"));
        dataLayer.updateOrInsert(awesomeEvent);
    }

    private void whenUpdateOrInsertEventAffectsRows(int numberOfAffectedRows) throws SQLException {
        when(mockConnectionManager.getMockDataProvider().execute(any()))
                .thenReturn(new MockResult[]{new MockResult(numberOfAffectedRows, null)});
    }

}
