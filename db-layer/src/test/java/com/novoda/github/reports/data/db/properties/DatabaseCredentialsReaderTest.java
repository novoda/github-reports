package com.novoda.github.reports.data.db.properties;

import com.novoda.github.reports.batch.properties.PropertiesReader;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class DatabaseCredentialsReaderTest {

    private static final String ANY_USER = "tim-riggins";
    private static final String ANY_PASSWORD = "texasforever";
    private static final String SIMPLE_CONNECTION_STRING = "jdbc:mysql://instance-address:3306";
    private static final String COMPLEX_CONNECTION_STRING = "jdbc:mysql://instance-address:3306?some=var&another=var";
    private static final String CONNECTION_STRING_QUERY_PART = "compensateOnDuplicateKeyUpdateCounts=true";

    @Mock
    PropertiesReader propertiesReader;

    @InjectMocks
    DatabaseCredentialsReader databaseCredentialsReader;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void givenPropertiesWithUser_whenGetUser_thenReturnUser() {
        when(propertiesReader.readProperty(DatabaseCredentialsReader.USER_KEY)).thenReturn(ANY_USER);

        String actualUser = databaseCredentialsReader.getUser();

        assertEquals(ANY_USER, actualUser);
    }

    @Test
    public void givenPropertiesWithPassword_whenGetPassword_thenReturnPassword() {
        when(propertiesReader.readProperty(DatabaseCredentialsReader.PASSWORD_KEY)).thenReturn(ANY_PASSWORD);

        String actualPassword = databaseCredentialsReader.getPassword();

        assertEquals(ANY_PASSWORD, actualPassword);
    }

    @Test
    public void givenPropertiesWithSimpleConnectionString_whenGetConnectionString_thenAddQueryPart() {
        when(propertiesReader.readProperty(DatabaseCredentialsReader.CONNECTION_STRING_KEY)).thenReturn(SIMPLE_CONNECTION_STRING);

        String actualConnectionString = null;
        try {
            actualConnectionString = databaseCredentialsReader.getConnectionString();
        } catch (URISyntaxException e) {
            fail();
        }

        assertEquals(SIMPLE_CONNECTION_STRING + "?" + CONNECTION_STRING_QUERY_PART, actualConnectionString);
    }

    @Test
    public void givenPropertiesWithComplexConnectionString_whenGetConnectionString_thenAddParameterToQueryPart() {
        when(propertiesReader.readProperty(DatabaseCredentialsReader.CONNECTION_STRING_KEY)).thenReturn(COMPLEX_CONNECTION_STRING);

        String actualConnectionString = null;
        try {
            actualConnectionString = databaseCredentialsReader.getConnectionString();
        } catch (URISyntaxException e) {
            fail();
        }

        assertEquals(COMPLEX_CONNECTION_STRING + "&" + CONNECTION_STRING_QUERY_PART, actualConnectionString);
    }

}
