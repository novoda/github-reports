package com.novoda.github.reports.sheets.properties;

import com.novoda.github.reports.properties.PropertiesReader;

public class DocumentIdReader {

    private static final String SHEETS_PROPERTIES_FILENAME = "sheets.credentials";
    private static final String DOCUMENT_ID_KEY = "DOCUMENT_ID";

    private PropertiesReader propertiesReader;

    public static DocumentIdReader newInstance() {
        PropertiesReader propertiesReader = PropertiesReader.newInstance(SHEETS_PROPERTIES_FILENAME);
        return new DocumentIdReader(propertiesReader);
    }

    private DocumentIdReader(PropertiesReader propertiesReader) {
        this.propertiesReader = propertiesReader;
    }

    public String getDocumentId() {
        return propertiesReader.readProperty(DOCUMENT_ID_KEY);
    }

}
