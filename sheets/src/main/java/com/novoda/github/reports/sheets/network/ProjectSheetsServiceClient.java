package com.novoda.github.reports.sheets.network;

import com.novoda.github.reports.sheets.convert.GithubRepositoryNameRemover;
import com.novoda.github.reports.sheets.convert.ValueRemover;
import com.novoda.github.reports.sheets.properties.DocumentIdReader;
import com.novoda.github.reports.sheets.sheet.Entry;

import rx.Observable;

public class ProjectSheetsServiceClient {

    private SheetsServiceClient sheetsServiceClient;
    private DocumentIdReader documentIdReader;

    public static ProjectSheetsServiceClient newInstance() {
        ValueRemover<Entry> keyRemover = new GithubRepositoryNameRemover();
        DocumentIdReader documentIdReader = DocumentIdReader.newInstance();
        SheetsServiceClient sheetsServiceClient = SheetsServiceClient.newInstance(keyRemover);
        return new ProjectSheetsServiceClient(sheetsServiceClient, documentIdReader);
    }

    ProjectSheetsServiceClient(SheetsServiceClient sheetsServiceClient, DocumentIdReader documentIdReader) {
        this.sheetsServiceClient = sheetsServiceClient;
        this.documentIdReader = documentIdReader;
    }

    public Observable<Entry> getProjectEntries() {
        String documentId = documentIdReader.getUsersDocumentId();
        return sheetsServiceClient.getEntries(documentId);
    }
}
