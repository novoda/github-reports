package com.novoda.github.reports.data.db;

import java.sql.Connection;
import java.sql.SQLException;

import org.jooq.DSLContext;

public interface ConnectionManager {

    Connection getNewConnection() throws SQLException;

    DSLContext getNewDSLContext(Connection connection);

    void attemptCloseConnection(Connection connection);

}
