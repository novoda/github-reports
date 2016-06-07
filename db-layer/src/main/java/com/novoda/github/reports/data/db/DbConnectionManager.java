package com.novoda.github.reports.data.db;

import com.novoda.github.reports.data.db.properties.DatabaseCredentialsReader;
import com.novoda.github.reports.properties.PropertiesReader;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

public class DbConnectionManager implements ConnectionManager {

    static {
        DatabaseHelper.turnOffJooqAd();
    }

    private static final String DATABASE_CREDENTIALS_FILENAME = "../database.credentials";
    private final DatabaseCredentialsReader databaseCredentialsReader;
    private DataSource dataSource;

    public static DbConnectionManager newInstance() {
        return new DbConnectionManager();
    }

    private DbConnectionManager() {
        databaseCredentialsReader = DatabaseCredentialsReader.newInstance(PropertiesReader.newInstance(DATABASE_CREDENTIALS_FILENAME));
    }

    @Override
    public Connection getNewConnection() throws SQLException {
        buildDataSource();
        return dataSource.getConnection();
    }

    private synchronized void buildDataSource() {
        if (dataSource != null) {
            return;
        }
        ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(
                databaseCredentialsReader.getConnectionString(),
                databaseCredentialsReader.getConnectionProperties()
        );

        PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, null);
        ObjectPool<PoolableConnection> connectionPool = new GenericObjectPool<>(poolableConnectionFactory);
        poolableConnectionFactory.setPool(connectionPool);

        dataSource = new PoolingDataSource<>(connectionPool);
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
