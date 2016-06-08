package com.novoda.github.reports.data.db;

import com.novoda.github.reports.data.DataLayerException;
import com.novoda.github.reports.data.model.Repository;

import java.sql.SQLException;

import org.jooq.tools.jdbc.MockResult;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.contains;
import static org.mockito.Mockito.when;

public class DbRepoDataLayerTest {
    private static final Long ANY_REPOSITORY_ID = 1337L;
    private static final String ANY_REPOSITORY_NAME = "awesome-stuff";
    private static final boolean ANY_REPOSITORY_PRIVATE = false;
    private DbRepoDataLayer dataLayer;
    private MockConnectionManager mockConnectionManager;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws SQLException {
        mockConnectionManager = MockConnectionManager.newInstance();
        dataLayer = DbRepoDataLayer.newInstance(mockConnectionManager);
    }

    @Test
    public void givenNewRepo_whenUpdateOrInsertRepo_thenReturnGivenRepo() throws SQLException {
        Repository expectedRepo = Repository.create(ANY_REPOSITORY_ID, ANY_REPOSITORY_NAME, ANY_REPOSITORY_PRIVATE);
        whenUpdateOrInsertRepoAffectsRows(1);

        Repository actualRepo = null;
        try {
            actualRepo = dataLayer.updateOrInsert(expectedRepo);
        } catch (DataLayerException e) {
            fail();
        }

        assertEquals(expectedRepo, actualRepo);
    }

    @Test
    public void givenInvalidDatabase_whenUpdateOrInsertRepo_thenThrowDataLayerException() throws SQLException, DataLayerException {
        Repository awesomeRepo = Repository.create(ANY_REPOSITORY_ID, ANY_REPOSITORY_NAME, ANY_REPOSITORY_PRIVATE);
        whenUpdateOrInsertRepoAffectsRows(3);

        thrown.expect(DataLayerException.class);
        thrown.expectMessage(contains("Unhandled"));
        dataLayer.updateOrInsert(awesomeRepo);
    }

    @Test
    public void givenValidRepo_whenUpdateOrInsertRepo_thenThrowDataLayerException() throws SQLException, DataLayerException {
        Repository awesomeRepo = Repository.create(ANY_REPOSITORY_ID, ANY_REPOSITORY_NAME, ANY_REPOSITORY_PRIVATE);
        whenUpdateOrInsertRepoAffectsRows(-1);

        thrown.expect(DataLayerException.class);
        thrown.expectMessage(contains("Could not"));
        dataLayer.updateOrInsert(awesomeRepo);
    }

    private void whenUpdateOrInsertRepoAffectsRows(int numberOfAffectedRows) throws SQLException {
        when(mockConnectionManager.getMockDataProvider().execute(any()))
                .thenReturn(new MockResult[]{new MockResult(numberOfAffectedRows, null)});
    }

}
