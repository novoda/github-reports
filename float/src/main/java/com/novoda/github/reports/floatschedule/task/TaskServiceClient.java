package com.novoda.github.reports.floatschedule.task;

import com.novoda.github.reports.floatschedule.network.FloatApiService;
import com.novoda.github.reports.floatschedule.network.FloatServiceContainer;

import retrofit2.Response;
import rx.Observable;

public class TaskServiceClient {

    private final FloatApiService floatApiService;

    public static TaskServiceClient newInstance() {
        FloatApiService floatApiService = FloatServiceContainer.getFloatService();
        return new TaskServiceClient(floatApiService);
    }

    TaskServiceClient(FloatApiService floatApiService) {
        this.floatApiService = floatApiService;
    }

    public Observable<Task> getTasks() {
        return floatApiService.getTasks()
                .map(Response::body)
                .map(Assignments::getAssignments)
                .flatMapIterable(assignments -> assignments)
                .map(Assignment::getTasks)
                .flatMapIterable(tasks -> tasks);
    }

}
