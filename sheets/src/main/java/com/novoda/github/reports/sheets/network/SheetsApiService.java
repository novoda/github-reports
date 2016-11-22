package com.novoda.github.reports.sheets.network;

import com.novoda.github.reports.sheets.sheet.Sheet;

import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

public interface SheetsApiService {

    @GET("{documentId}/1/public/basic?alt=json")
    Observable<Response<Sheet>> getDocument(@Path("documentId") String documentId);

}
