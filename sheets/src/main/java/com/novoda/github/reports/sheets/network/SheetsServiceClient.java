package com.novoda.github.reports.sheets.network;

import com.novoda.github.reports.sheets.convert.GithubUsernameRemover;
import com.novoda.github.reports.sheets.convert.ValueRemover;
import com.novoda.github.reports.sheets.properties.DocumentIdReader;
import com.novoda.github.reports.sheets.sheet.Entry;

import rx.Observable;

public class SheetsServiceClient {

    private final SheetsApiService apiService;
    private final ValueRemover<Entry> keyRemover;
    private final DocumentIdReader documentIdReader;

    public static SheetsServiceClient newInstance() {
        SheetsApiService apiService = SheetsServiceContainer.getSheetsService();
        ValueRemover<Entry> keyRemover = new GithubUsernameRemover();
        DocumentIdReader documentIdReader = DocumentIdReader.newInstance();
        return new SheetsServiceClient(apiService, keyRemover, documentIdReader);
    }

    SheetsServiceClient(SheetsApiService apiService, ValueRemover<Entry> keyRemover, DocumentIdReader documentIdReader) {
        this.apiService = apiService;
        this.keyRemover = keyRemover;
        this.documentIdReader = documentIdReader;
    }

    public Observable<Entry> getEntries() {
        String documentId = documentIdReader.getDocumentId();
         return apiService.getEntries(documentId)
                 .map(keyRemover::removeFrom);
    }

}
