package com.novoda.github.reports.sheets.network;

import com.novoda.github.reports.sheets.convert.GithubRepositoryNameRemover;
import com.novoda.github.reports.sheets.convert.ValueRemover;
import com.novoda.github.reports.sheets.properties.DocumentIdReader;
import com.novoda.github.reports.sheets.sheet.Entry;

import rx.Observable;

public class ProjectSheetsServiceClient {

    private SheetsServiceClient sheetsServiceClient;

    public static ProjectSheetsServiceClient newInstance() {
        SheetsApiService apiService = SheetsServiceContainer.getSheetsService();
        ValueRemover<Entry> keyRemover = new GithubRepositoryNameRemover();
        DocumentIdReader documentIdReader = DocumentIdReader.newInstance();
        SheetsServiceClient sheetsServiceClient = new SheetsServiceClient(apiService, keyRemover, documentIdReader);
        return new ProjectSheetsServiceClient(sheetsServiceClient);
    }

    ProjectSheetsServiceClient(SheetsServiceClient sheetsServiceClient) {
        this.sheetsServiceClient = sheetsServiceClient;
    }

    public Observable<Entry> getProjectEntries() {
        return null;
    }
}
