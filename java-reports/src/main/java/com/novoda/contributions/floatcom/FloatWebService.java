package com.novoda.contributions.floatcom;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;

interface FloatWebService {

    @Headers({"Content-Type: application/x-www-form-urlencoded", "Accept: application/json"})
    @GET("people?active=1")
    Call<ApiPeople> getPeople(@Header("Authorization") String authorization);

    @Headers({"Content-Type: application/x-www-form-urlencoded", "Accept: application/json"})
    @GET("tasks?active=1")
    Call<ApiTasks> getTasks(@Header("Authorization") String authorization,
                            @Query("start_day") String startDay,
                            @Query("weeks") int weeks);

}
