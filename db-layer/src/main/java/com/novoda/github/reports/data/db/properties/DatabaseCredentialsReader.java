package com.novoda.github.reports.data.db.properties;

import com.novoda.github.reports.properties.PropertiesReader;

import java.util.Properties;

public class DatabaseCredentialsReader {

    public static final String PROPERTY_USERNAME = "user";
    public static final String PROPERTY_PASSWORD = "password";

    static final String USER_KEY = "DB_USER";
    static final String PASSWORD_KEY = "DB_PASSWORD";
    static final String CONNECTION_STRING_KEY = "DB_CONNECTION_STRING";

    private PropertiesReader propertiesReader;

    public static DatabaseCredentialsReader newInstance(PropertiesReader propertiesReader) {
        return new DatabaseCredentialsReader(propertiesReader);
    }

    private DatabaseCredentialsReader(PropertiesReader propertiesReader) {
        this.propertiesReader = propertiesReader;
    }

    public String getConnectionString() {
        return propertiesReader.readProperty(CONNECTION_STRING_KEY);
    }

    public Properties getConnectionProperties() {
        Properties properties = new Properties();
        properties.setProperty(PROPERTY_USERNAME, getUser());
        properties.setProperty(PROPERTY_PASSWORD, getPassword());
        return properties;
    }

    private String getUser() {
        return propertiesReader.readProperty(USER_KEY);
    }

    private String getPassword() {
        return propertiesReader.readProperty(PASSWORD_KEY);
    }
}
