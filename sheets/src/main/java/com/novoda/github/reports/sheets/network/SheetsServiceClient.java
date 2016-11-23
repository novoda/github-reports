package com.novoda.github.reports.sheets.network;

import com.novoda.github.reports.sheets.convert.GithubUsernameRemover;
import com.novoda.github.reports.sheets.convert.ValueRemover;
import com.novoda.github.reports.sheets.sheet.Entry;

import rx.Observable;

public class SheetsServiceClient {

    // TODO extract to .json and read as prop
    private static final String DOCUMENT_ID = "1rMeGnlugO312to0loBwN3x0QTvAxoHwv4Pe_SYXR1YE";

    private final SheetsApiService apiService;
    private final ValueRemover<Entry> keyRemover;

    public static SheetsServiceClient newInstance() {
        SheetsApiService apiService = SheetsServiceContainer.getSheetsService();
        ValueRemover<Entry> keyRemover = new GithubUsernameRemover();
        return new SheetsServiceClient(apiService, keyRemover);
    }

    SheetsServiceClient(SheetsApiService apiService, ValueRemover<Entry> keyRemover) {
        this.apiService = apiService;
        this.keyRemover = keyRemover;
    }

    public Observable<Entry> getEntries() {
         return apiService.getEntries(DOCUMENT_ID)
                 .map(keyRemover::removeFrom);
    }

}
