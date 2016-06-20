package com.novoda.github.reports.batch.configuration;

import com.google.auto.value.AutoValue;
import com.novoda.github.reports.data.db.properties.DatabaseCredentialsReader;

import java.util.Properties;

@AutoValue
public abstract class DatabaseConfiguration {

    public static DatabaseConfiguration create(String connectionString, String username, String password) {
        Properties connectionProperties = new Properties();
        connectionProperties.put(DatabaseCredentialsReader.PROPERTY_USERNAME, username);
        connectionProperties.put(DatabaseCredentialsReader.PROPERTY_PASSWORD, password);
        return create(connectionString, connectionProperties);
    }

    public static DatabaseConfiguration create(DatabaseCredentialsReader databaseCredentialsReader) {
        return create(databaseCredentialsReader.getConnectionString(), databaseCredentialsReader.getConnectionProperties());
    }

    public static DatabaseConfiguration create(String connectionString, Properties connectionProperties) {
        return new AutoValue_DatabaseConfiguration(connectionString, connectionProperties);
    }

    public String username() {
        return connectionProperties().getProperty(DatabaseCredentialsReader.PROPERTY_USERNAME);
    }

    public String password() {
        return connectionProperties().getProperty(DatabaseCredentialsReader.PROPERTY_PASSWORD);
    }

    public abstract String connectionString();

    public abstract Properties connectionProperties();
}
