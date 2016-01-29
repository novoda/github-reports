package com.novoda.contributions;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;

public interface FloatWebService {

    @Headers({"Content-Type: application/x-www-form-urlencoded", "Accept: application/json"})
    @GET("projects?active=1")
    Call<ApiProjects> getProjects(@Header("Authorization") String authorization);

    @Headers({"Content-Type: application/x-www-form-urlencoded", "Accept: application/json"})
    @GET("people?active=1")
    Call<ApiPeople> getPeople(@Header("Authorization") String authorization);

}
