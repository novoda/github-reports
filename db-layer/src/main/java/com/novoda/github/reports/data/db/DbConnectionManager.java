package com.novoda.github.reports.data.db;

import com.novoda.github.reports.data.db.properties.DatabaseCredentialsReader;
import com.novoda.github.reports.properties.PropertiesReader;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

public class DbConnectionManager implements ConnectionManager {

    static {
        DatabaseHelper.turnOffJooqAd();
    }

    private static final String DATABASE_CREDENTIALS_FILENAME = "database.credentials";
    private final DatabaseCredentialsReader databaseCredentialsReader;

    public static DbConnectionManager newInstance() {
        return new DbConnectionManager();
    }

    private DbConnectionManager() {
        databaseCredentialsReader = DatabaseCredentialsReader.newInstance(PropertiesReader.newInstance(DATABASE_CREDENTIALS_FILENAME));
    }

    @Override
    public Connection getNewConnection() throws SQLException {
        try {
            return DriverManager.getConnection(
                    databaseCredentialsReader.getConnectionString(),
                    databaseCredentialsReader.getUser(),
                    databaseCredentialsReader.getPassword()
            );
        } catch (URISyntaxException e) {
            throw new SQLException(e);
        }
    }

    @Override
    public DSLContext getNewDSLContext(Connection connection) {
        return DSL.using(connection, SQLDialect.MYSQL);
    }

    @Override
    public void attemptCloseConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ignored) {
            }
        }
    }

}
