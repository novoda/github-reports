package com.novoda.github.reports.batch.persistence;

import com.novoda.github.reports.data.db.ConnectionManager;
import com.novoda.github.reports.data.db.DbConnectionManager;

public class ConnectionManagerContainer {

    private final static ConnectionManager connectionManager = DbConnectionManager.newInstance();

    private ConnectionManagerContainer() {
        // not-instantiable
    }

    public static ConnectionManager getConnectionManager() {
        return connectionManager;
    }
}
