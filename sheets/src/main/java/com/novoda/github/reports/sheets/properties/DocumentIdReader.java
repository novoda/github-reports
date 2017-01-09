package com.novoda.github.reports.sheets.properties;

import com.novoda.github.reports.properties.PropertiesReader;

public class DocumentIdReader {

    private static final String SHEETS_PROPERTIES_FILENAME = "sheets.credentials";
    private static final String USERS_DOCUMENT_ID_KEY = "USERS_DOCUMENT_ID";
    private static final String PROJECTS_DOCUMENT_ID_KEY = "PROJECTS_DOCUMENT_ID";

    private PropertiesReader propertiesReader;

    public static DocumentIdReader newInstance() {
        PropertiesReader propertiesReader = PropertiesReader.newInstance(SHEETS_PROPERTIES_FILENAME);
        return new DocumentIdReader(propertiesReader);
    }

    private DocumentIdReader(PropertiesReader propertiesReader) {
        this.propertiesReader = propertiesReader;
    }

    public String getUsersDocumentId() {
        return propertiesReader.readProperty(USERS_DOCUMENT_ID_KEY);
    }

    public String getProjectsDocumentId() {
        return propertiesReader.readProperty(PROJECTS_DOCUMENT_ID_KEY);
    }

}
