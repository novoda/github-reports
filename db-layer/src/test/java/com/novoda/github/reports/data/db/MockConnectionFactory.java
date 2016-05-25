package com.novoda.github.reports.data.db;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.tools.jdbc.MockConnection;
import org.jooq.tools.jdbc.MockResult;

public class MockConnectionFactory implements ConnectionFactory {

    public static MockConnectionFactory newInstance() {
        return new MockConnectionFactory();
    }

    @Override
    public Connection getNewConnection() {
        return new MockConnection(ctx -> new MockResult[0]);
    }

    @Override
    public DSLContext getNewDSLContext(Connection connection) {
        return DSL.using(connection, SQLDialect.MYSQL);
    }

    @Override
    public void attemptCloseConnection(Connection connection) {

    }
}
