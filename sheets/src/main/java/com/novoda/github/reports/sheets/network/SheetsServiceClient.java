package com.novoda.github.reports.sheets.network;

import com.novoda.github.reports.sheets.sheet.Content;
import com.novoda.github.reports.sheets.sheet.Entry;
import com.novoda.github.reports.sheets.sheet.Sheet;

import retrofit2.Response;
import rx.Observable;
import rx.functions.Func1;

public class SheetsServiceClient {

    // TODO extract to .json and read as prop
    private static final String DOCUMENT_ID = "1rMeGnlugO312to0loBwN3x0QTvAxoHwv4Pe_SYXR1YE";

    private final SheetsApiService apiService;

    public static SheetsServiceClient newInstance() {
        SheetsApiService apiService = SheetsServiceContainer.getSheetsService();
        return new SheetsServiceClient(apiService);
    }

    SheetsServiceClient(SheetsApiService apiService) {
        this.apiService = apiService;
    }

    public Observable<Entry> getEntries() {
         return apiService.getDocument(DOCUMENT_ID)
                 .flatMap(toEntries())
                 .map(entry -> {
                     Content value = new Content(
                             entry.getValue().getType(),
                             entry.getValue().getValue().replace("githubuser: ", "")
                     );
                     return new Entry(entry.getKey(), value);
                 })
                 ;
    }

    private Func1<Response<Sheet>, Observable<Entry>> toEntries() {
        return sheetResponse -> Observable.from(sheetResponse.body().getFeed().getEntries());
    }

}
