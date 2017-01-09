package com.novoda.github.reports.sheets.network;

import com.novoda.github.reports.sheets.properties.DocumentIdReader;
import com.novoda.github.reports.sheets.sheet.Entry;

import rx.Observable;

public class ProjectSheetsServiceClient {

    private SheetsServiceClient sheetsServiceClient;
    private DocumentIdReader documentIdReader;

    public static ProjectSheetsServiceClient newInstance() {
        DocumentIdReader documentIdReader = DocumentIdReader.newInstance();
        SheetsServiceClient sheetsServiceClient = SheetsServiceClient.newInstance();
        return new ProjectSheetsServiceClient(sheetsServiceClient, documentIdReader);
    }

    ProjectSheetsServiceClient(SheetsServiceClient sheetsServiceClient, DocumentIdReader documentIdReader) {
        this.sheetsServiceClient = sheetsServiceClient;
        this.documentIdReader = documentIdReader;
    }

    public Observable<Entry> getProjectEntries() {
        String documentId = documentIdReader.getProjectsDocumentId();
        return sheetsServiceClient.getEntries(documentId);
    }
}
