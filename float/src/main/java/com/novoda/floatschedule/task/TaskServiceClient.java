package com.novoda.floatschedule.task;

import com.novoda.floatschedule.convert.FloatDateConverter;
import com.novoda.floatschedule.network.FloatApiService;
import com.novoda.floatschedule.network.FloatServiceContainer;
import retrofit2.Response;
import rx.Observable;

import java.util.Date;

public class TaskServiceClient {

    private final FloatApiService floatApiService;
    private final FloatDateConverter floatDateConverter;

    public static TaskServiceClient newInstance() {
        FloatApiService floatApiService = FloatServiceContainer.getFloatService();
        FloatDateConverter floatDateConverter = new FloatDateConverter();
        return new TaskServiceClient(floatApiService, floatDateConverter);
    }

    TaskServiceClient(FloatApiService floatApiService, FloatDateConverter floatDateConverter) {
        this.floatApiService = floatApiService;
        this.floatDateConverter = floatDateConverter;
    }

    public Observable<Task> getTasksForAllPeople(Date startDate, Integer numberOfWeeks) {
        return getTasks(startDate, numberOfWeeks, null);
    }

    public Observable<Task> getTasks(Date startDate, Integer numberOfWeeks, Integer personId) {
        String date = floatDateConverter.toFloatDateFormat(startDate);
        return floatApiService.getTasks(date, numberOfWeeks, personId)
                .map(Response::body)
                .map(Assignments::getAssignments)
                .flatMapIterable(assignments -> assignments)
                .map(Assignment::getTasks)
                .flatMapIterable(tasks -> tasks);
    }

}
