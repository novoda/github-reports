package com.novoda.github.reports.sheets.network;

import com.novoda.github.reports.sheets.convert.GithubUsernameRemover;
import com.novoda.github.reports.sheets.convert.ValueRemover;
import com.novoda.github.reports.sheets.properties.DocumentIdReader;
import com.novoda.github.reports.sheets.sheet.Entry;

import rx.Observable;

public class UserSheetsServiceClient {

    private SheetsServiceClient sheetsServiceClient;

    public static UserSheetsServiceClient newInstance() {
        // TODO @RUI rework this construction
        SheetsApiService apiService = SheetsServiceContainer.getSheetsService();
        ValueRemover<Entry> keyRemover = new GithubUsernameRemover();
        DocumentIdReader documentIdReader = DocumentIdReader.newInstance();
        SheetsServiceClient sheetsServiceClient = new SheetsServiceClient(apiService, keyRemover, documentIdReader);
        return new UserSheetsServiceClient(sheetsServiceClient);
    }

    UserSheetsServiceClient(SheetsServiceClient sheetsServiceClient) {
        this.sheetsServiceClient = sheetsServiceClient;
    }

    public Observable<Entry> getUserEntries() {
        return sheetsServiceClient.getEntries();
    }
}
