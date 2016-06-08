package com.novoda.github.reports.data.db.properties;

import com.novoda.github.reports.service.properties.PropertiesReader;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class DatabaseCredentialsReaderTest {

    private static final String ANY_USER = "tim-riggins";
    private static final String ANY_PASSWORD = "texasforever";
    private static final String SIMPLE_CONNECTION_STRING = "jdbc:mysql://instance-address:3306";

    @Mock
    PropertiesReader propertiesReader;

    @InjectMocks
    DatabaseCredentialsReader databaseCredentialsReader;

    @Before
    public void setUp() {
        initMocks(this);
        when(propertiesReader.readProperty(DatabaseCredentialsReader.USER_KEY)).thenReturn(ANY_USER);
        when(propertiesReader.readProperty(DatabaseCredentialsReader.PASSWORD_KEY)).thenReturn(ANY_PASSWORD);
    }

    @Test
    public void givenPropertiesWithUser_whenGetUserFromProperties_thenReturnUser() {
        String actualUser = databaseCredentialsReader.getConnectionProperties().getProperty(DatabaseCredentialsReader.PROPERTY_USERNAME);

        assertEquals(ANY_USER, actualUser);
    }

    @Test
    public void givenPropertiesWithPassword_whenGetPasswordFromProperties_thenReturnPassword() {
        String actualPassword = databaseCredentialsReader.getConnectionProperties().getProperty(DatabaseCredentialsReader.PROPERTY_PASSWORD);

        assertEquals(ANY_PASSWORD, actualPassword);
    }

    @Test
    public void givenPropertiesWithSimpleConnectionString_whenGetConnectionString_thenReturnConnectionString() {
        when(propertiesReader.readProperty(DatabaseCredentialsReader.CONNECTION_STRING_KEY)).thenReturn(SIMPLE_CONNECTION_STRING);

        String actualConnectionString = databaseCredentialsReader.getConnectionString();

        assertEquals(SIMPLE_CONNECTION_STRING, actualConnectionString);
    }

}
