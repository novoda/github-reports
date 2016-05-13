package com.novoda.github.reports.data.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

class ConnectionManager {
    static Connection getNewConnection() throws SQLException {
        return DriverManager.getConnection(BuildConfig.DATABASE_URL, BuildConfig.DATABASE_USERNAME, BuildConfig.DATABASE_PASSWORD);
    }

    static DSLContext getNewDSLContext(Connection connection) {
        return DSL.using(connection, SQLDialect.MYSQL);
    }

    static void attemptCloseConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ignored) {
            }
        }
    }
}
