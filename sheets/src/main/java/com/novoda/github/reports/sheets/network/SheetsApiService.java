package com.novoda.github.reports.sheets.network;

import com.novoda.github.reports.sheets.sheet.Sheet;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

public interface SheetsApiService {


    //https://spreadsheets.google.com/feeds/list/1rMeGnlugO312to0loBwN3x0QTvAxoHwv4Pe_SYXR1YE/1/public/basic?alt=json

    @GET("{documentId}/1/public/basic?alt=json")
    Observable<Response<Sheet>> getDocument(@Path("documentId") String documentId);

    @GET("{documentId}/1/public/basic?alt=json")
    Call<String> debugGetDocument(@Path("documentId") String documentId);
}
