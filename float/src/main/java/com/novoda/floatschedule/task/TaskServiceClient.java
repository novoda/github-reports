package com.novoda.floatschedule.task;

import com.novoda.floatschedule.convert.FloatDateConverter;
import com.novoda.floatschedule.network.FloatApiService;
import com.novoda.floatschedule.network.FloatServiceContainer;
import retrofit2.Response;
import rx.Observable;

import java.util.Date;
import java.util.TimeZone;

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

    public Observable<Task> getTasksForAllPeople(Date startDate, Integer numberOfWeeks, TimeZone timezone) {
        return getTasks(startDate, numberOfWeeks, timezone, null);
    }

    public Observable<Task> getTasks(Date startDate, Integer numberOfWeeks, TimeZone timezone, Integer personId) {
        String date = floatDateConverter.toFloatDateFormat(startDate, timezone);
        return floatApiService.getTasks(date, numberOfWeeks, personId)
                .map(Response::body)
                .map(Assignments::getAssignments)
                .flatMapIterable(assignments -> assignments)
                .map(Assignment::getTasks)
                .flatMapIterable(tasks -> tasks);
    }

}
