package com.novoda.github.reports.floatschedule.project;

import com.novoda.github.reports.floatschedule.network.FloatApiService;
import com.novoda.github.reports.floatschedule.network.FloatServiceContainer;

import retrofit2.Response;
import rx.Observable;

public class ProjectServiceClient {

    private final FloatApiService floatApiService;

    public static ProjectServiceClient newInstance() {
        FloatApiService floatApiService = FloatServiceContainer.getFloatService();
        return new ProjectServiceClient(floatApiService);
    }

    ProjectServiceClient(FloatApiService floatApiService) {
        this.floatApiService = floatApiService;
    }

    public Observable<Project> getProjects() {
        return floatApiService.getProjects()
                .map(Response::body)
                .map(Projects::getProjects)
                .flatMapIterable(p -> p);
    }
}
