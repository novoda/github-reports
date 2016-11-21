package com.novoda.github.reports.sheets.network;

import com.novoda.github.reports.sheets.sheet.Sheet;

import java.io.IOException;

import rx.Observable;

public class SheetsServiceClient {

    // TODO extract to .json and read as prop
    private static final String DOCUMENT_ID = "1rMeGnlugO312to0loBwN3x0QTvAxoHwv4Pe_SYXR1YE";

    private final SheetsApiService apiService;

    public static SheetsServiceClient newInstance() {
        SheetsApiService apiService = SheetsServiceContainer.getSheetsService();
        return new SheetsServiceClient(apiService);
    }

    private SheetsServiceClient(SheetsApiService apiService) {
        this.apiService = apiService;
    }

    // TODO should return obs of map<string,string>, or map.entry<string,string>, or something else?

    public Observable<Sheet> getDocument() {
        return apiService.getDocument(DOCUMENT_ID)
                .map(stringResponse -> stringResponse.body());
    }

    public String debugGetDocument() {

        try {
            return apiService.debugGetDocument(DOCUMENT_ID).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

}
