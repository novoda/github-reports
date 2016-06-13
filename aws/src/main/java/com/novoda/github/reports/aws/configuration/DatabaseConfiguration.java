package com.novoda.github.reports.aws.configuration;

import com.google.auto.value.AutoValue;
import com.novoda.github.reports.data.db.properties.DatabaseCredentialsReader;

import java.util.Properties;

@AutoValue
public abstract class DatabaseConfiguration {

    public static DatabaseConfiguration create(String connectionString, Properties connectionProperties) {
        return new AutoValue_DatabaseConfiguration(connectionString, connectionProperties);
    }

    public static DatabaseConfiguration create(DatabaseCredentialsReader databaseCredentialsReader) {
        return create(databaseCredentialsReader.getConnectionString(), databaseCredentialsReader.getConnectionProperties());
    }

    abstract String connectionString();

    abstract Properties connectionProperties();
}
