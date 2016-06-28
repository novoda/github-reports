package com.novoda.floatschedule.task;

import com.novoda.floatschedule.network.FloatApiService;
import com.novoda.floatschedule.network.FloatServiceContainer;

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
        return getTasks(null, null, null);
    }

    public Observable<Task> getTasks(String date, Integer numberOfWeeks, Integer personId) {
        return floatApiService.getTasks(date, numberOfWeeks, personId)
                .map(Response::body)
                .map(Assignments::getAssignments)
                .flatMapIterable(assignments -> assignments)
                .map(Assignment::getTasks)
                .flatMapIterable(tasks -> tasks);
    }

}
