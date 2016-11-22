package com.novoda.github.reports.sheets.network;

import com.novoda.github.reports.sheets.convert.GithubUsernameRemover;
import com.novoda.github.reports.sheets.convert.ValueRemover;
import com.novoda.github.reports.sheets.sheet.Entry;
import com.novoda.github.reports.sheets.sheet.Sheet;

import retrofit2.Response;
import rx.Observable;
import rx.functions.Func1;

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

    public Observable<Entry> _getEntries() {
         return apiService._getDocument(DOCUMENT_ID)
                 .map(keyRemover::removeFrom);
    }

    public Observable<Entry> getEntries() {
         return apiService.getDocument(DOCUMENT_ID)
                 .flatMap(toEntries())
                 .map(keyRemover::removeFrom);
    }

    private Func1<Response<Sheet>, Observable<Entry>> toEntries() {
        return sheetResponse -> Observable.from(sheetResponse.body().getFeed().getEntries());
    }

}
