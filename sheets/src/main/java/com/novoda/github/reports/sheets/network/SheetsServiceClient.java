package com.novoda.github.reports.sheets.network;

import com.novoda.github.reports.sheets.convert.ValueRemover;
import com.novoda.github.reports.sheets.sheet.Entry;

import rx.Observable;

public class SheetsServiceClient {

    private final SheetsApiService apiService;
    private final ValueRemover<Entry> keyRemover;

    public static SheetsServiceClient newInstance(ValueRemover<Entry> keyRemover) {
        SheetsApiService apiService = SheetsServiceContainer.getSheetsService();
        return new SheetsServiceClient(apiService, keyRemover);
    }

    SheetsServiceClient(SheetsApiService apiService, ValueRemover<Entry> keyRemover) {
        this.apiService = apiService;
        this.keyRemover = keyRemover;
    }

    public Observable<Entry> getEntries(String documentId) {
         return apiService.getEntries(documentId)
                 .map(keyRemover::removeFrom);
    }

}
