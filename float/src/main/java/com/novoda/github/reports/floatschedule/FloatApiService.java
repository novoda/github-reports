package com.novoda.github.reports.floatschedule;

import java.util.List;

import retrofit2.Response;
import retrofit2.http.GET;
import rx.Observable;

public interface FloatApiService {

    @GET("/people")
    Observable<Response<List<People>>> getPeople();

}
