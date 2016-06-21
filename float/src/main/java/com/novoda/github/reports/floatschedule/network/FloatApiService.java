package com.novoda.github.reports.floatschedule.network;

import com.novoda.github.reports.floatschedule.people.People;
import com.novoda.github.reports.floatschedule.project.Projects;

import retrofit2.Response;
import retrofit2.http.GET;
import rx.Observable;

public interface FloatApiService {

    @GET("people")
    Observable<Response<People>> getPeople();

    @GET("projects")
    Observable<Response<Projects>> getProjects();
}
