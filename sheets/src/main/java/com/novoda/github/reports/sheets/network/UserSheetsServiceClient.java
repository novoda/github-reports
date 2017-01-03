package com.novoda.github.reports.sheets.network;

import com.novoda.github.reports.sheets.properties.DocumentIdReader;
import com.novoda.github.reports.sheets.sheet.Entry;

import rx.Observable;

public class UserSheetsServiceClient {

    private DocumentIdReader documentIdReader;
    private SheetsServiceClient sheetsServiceClient;

    public static UserSheetsServiceClient newInstance() {
        DocumentIdReader documentIdReader = DocumentIdReader.newInstance();
        SheetsServiceClient sheetsServiceClient = SheetsServiceClient.newInstance();
        return new UserSheetsServiceClient(sheetsServiceClient, documentIdReader);
    }

    UserSheetsServiceClient(SheetsServiceClient sheetsServiceClient, DocumentIdReader documentIdReader) {
        this.sheetsServiceClient = sheetsServiceClient;
        this.documentIdReader = documentIdReader;
    }

    public Observable<Entry> getUserEntries() {
        String documentId = documentIdReader.getUsersDocumentId();
        return sheetsServiceClient.getEntries(documentId);
    }
}
