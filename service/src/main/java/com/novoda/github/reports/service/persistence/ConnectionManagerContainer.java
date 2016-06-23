package com.novoda.github.reports.service.persistence;

import com.novoda.github.reports.data.db.ConnectionManager;
import com.novoda.github.reports.data.db.DbConnectionManager;
import com.novoda.github.reports.data.db.properties.DatabaseCredentialsReader;

public class ConnectionManagerContainer {

    private static ConnectionManager connectionManager;

    private ConnectionManagerContainer() {
        // not-instantiable
    }

    public static ConnectionManager getConnectionManager() {
        if (connectionManager == null) {
            connectionManager = DbConnectionManager.newInstance();
        }
        return connectionManager;
    }

    public static ConnectionManager getConnectionManager(DatabaseCredentialsReader databaseCredentialsReader) {
        if (connectionManager == null) {
            connectionManager = DbConnectionManager.newInstance(databaseCredentialsReader);
        }
        return connectionManager;
    }
}
