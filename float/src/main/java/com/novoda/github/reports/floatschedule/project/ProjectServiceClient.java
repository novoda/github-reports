package com.novoda.github.reports.floatschedule.project;

import com.novoda.github.reports.floatschedule.network.FloatApiService;

import retrofit2.Response;
import rx.Observable;

public class ProjectServiceClient {

    private final FloatApiService floatApiService;

    public ProjectServiceClient(FloatApiService floatApiService) {
        this.floatApiService = floatApiService;
    }

    public Observable<Project> getProjects() {
        return floatApiService.getProjects()
                .flatMapIterable(Response::body);
    }
}
