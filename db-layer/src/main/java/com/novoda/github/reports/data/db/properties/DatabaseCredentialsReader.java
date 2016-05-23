package com.novoda.github.reports.data.db.properties;

import com.novoda.github.reports.properties.PropertiesReader;

public class DatabaseCredentialsReader {

    private static final String FILENAME = "database.credentials";
    private static final String USER_KEY = "DB_USER";
    private static final String PASSWORD_KEY = "DB_PASSWORD";
    private static final String CONNECTION_STRING_KEY = "DB_CONNECTION_STRING";

    private PropertiesReader propertiesReader;

    public static DatabaseCredentialsReader newInstance() {
        PropertiesReader propertiesReader = PropertiesReader.newInstance(FILENAME);
        return new DatabaseCredentialsReader(propertiesReader);
    }

    private DatabaseCredentialsReader(PropertiesReader propertiesReader) {
        this.propertiesReader = propertiesReader;
    }

    public String getUser() {
        return propertiesReader.readProperty(USER_KEY);
    }

    public String getPassword() {
        return propertiesReader.readProperty(PASSWORD_KEY);
    }

    public String getConnectionString() {
        return propertiesReader.readProperty(CONNECTION_STRING_KEY);
    }
}
