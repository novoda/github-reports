package com.novoda.github.reports.data.db;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.tools.jdbc.MockConnection;
import org.jooq.tools.jdbc.MockDataProvider;
import org.mockito.Mock;

import static org.mockito.MockitoAnnotations.initMocks;

class MockConnectionManager implements ConnectionManager {

    static {
        DatabaseHelper.turnOffJooqAd();
    }

    @Mock
    MockDataProvider mockDataProvider;

    static MockConnectionManager newInstance() {
        MockConnectionManager connectionManager = new MockConnectionManager();
        initMocks(connectionManager);
        return connectionManager;
    }

    @Override
    public Connection getNewConnection() {
        return new MockConnection(mockDataProvider);
    }

    @Override
    public DSLContext getNewDSLContext(Connection connection) {
        return DSL.using(connection, SQLDialect.MYSQL);
    }

    @Override
    public void attemptCloseConnection(Connection connection) {
        // Do nothing
    }

    public MockDataProvider getMockDataProvider() {
        return mockDataProvider;
    }
}
