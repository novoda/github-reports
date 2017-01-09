package com.novoda.github.reports.sheets.network;

import com.novoda.github.reports.sheets.convert.ContentHeaderRemover;
import com.novoda.github.reports.sheets.sheet.Entry;

import rx.Observable;

public class SheetsServiceClient {

    private final SheetsApiService apiService;
    private final ContentHeaderRemover contentHeaderRemover;

    public static SheetsServiceClient newInstance() {
        SheetsApiService apiService = SheetsServiceContainer.getSheetsService();
        ContentHeaderRemover contentHeaderRemover = new ContentHeaderRemover();
        return new SheetsServiceClient(apiService, contentHeaderRemover);
    }

    SheetsServiceClient(SheetsApiService apiService, ContentHeaderRemover contentHeaderRemover) {
        this.apiService = apiService;
        this.contentHeaderRemover = contentHeaderRemover;
    }

    public Observable<Entry> getEntries(String documentId) {
         return apiService.getEntries(documentId)
                 .map(contentHeaderRemover::removeFrom);
    }

}
