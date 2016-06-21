package com.novoda.github.reports.floatschedule.people;

import com.novoda.github.reports.floatschedule.network.FloatApiService;
import com.novoda.github.reports.floatschedule.network.FloatServiceContainer;

import retrofit2.Response;
import rx.Observable;

public class PeopleServiceClient {

    private final FloatApiService floatApiService;

    public static PeopleServiceClient newInstance() {
        FloatApiService floatApiService = FloatServiceContainer.getFloatService();
        return new PeopleServiceClient(floatApiService);
    }

    PeopleServiceClient(FloatApiService floatApiService) {
        this.floatApiService = floatApiService;
    }

    public Observable<Person> getPersons() {
        return floatApiService.getPeople()
                .map(Response::body)
                .map(People::getPersons)
                .flatMapIterable(persons -> persons);
    }
}
