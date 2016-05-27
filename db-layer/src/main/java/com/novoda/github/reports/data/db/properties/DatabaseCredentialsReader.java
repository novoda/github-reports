package com.novoda.github.reports.data.db.properties;

import com.novoda.github.reports.properties.PropertiesReader;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

public class DatabaseCredentialsReader {

    private static final String USER_KEY = "DB_USER";
    private static final String PASSWORD_KEY = "DB_PASSWORD";
    private static final String CONNECTION_STRING_KEY = "DB_CONNECTION_STRING";
    private static final String CONNECTION_STRING_COMPENSATE_ON_DUPLICATE_KEY_UPDATE_COUNTS = "compensateOnDuplicateKeyUpdateCounts";

    private PropertiesReader propertiesReader;

    public static DatabaseCredentialsReader newInstance(PropertiesReader propertiesReader) {
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

    public String getConnectionString() throws URISyntaxException {
        String baseConnection = propertiesReader.readProperty(CONNECTION_STRING_KEY);
        URI uri = URI.create(baseConnection.replaceAll("^jdbc:", ""));

        String query = uri.getQuery();
        query = (query == null) ? "" : "&";
        query += CONNECTION_STRING_COMPENSATE_ON_DUPLICATE_KEY_UPDATE_COUNTS + "=";
        try {
            query += URLEncoder.encode("true", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            query += "true";
        }

        URI actualUri = new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), query, uri.getFragment());
        return "jdbc:" + actualUri.toString();
    }
}
