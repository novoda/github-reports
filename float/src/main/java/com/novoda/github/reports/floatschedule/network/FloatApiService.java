package com.novoda.github.reports.floatschedule.network;

import com.novoda.github.reports.floatschedule.people.People;
import com.novoda.github.reports.floatschedule.project.Project;

import java.util.List;

import retrofit2.Response;
import retrofit2.http.GET;
import rx.Observable;

public interface FloatApiService {

    @GET("/people")
    Observable<Response<List<People>>> getPeople();

    @GET("/project")
    Observable<Response<List<Project>>> getProjects();
}
