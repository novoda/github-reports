package com.novoda.github.reports.floatschedule.people;

import com.novoda.github.reports.floatschedule.network.FloatApiService;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import retrofit2.Response;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PeopleServiceClientTest {

    @Mock
    FloatApiService mockFloatApiService;

    private TestSubscriber<Person> testSubscriber;

    private Observable<Response<People>> apiObservable;

    private PeopleServiceClient peopleServiceClient;

    private Person[] persons = new Person[]{
            new Person(23, "pirata"),
            new Person(88, "afonso"),
            new Person(42, "henriques")
    };

    @Before
    public void setUp() {
        initMocks(this);

        testSubscriber = new TestSubscriber<>();

        peopleServiceClient = new PeopleServiceClient(mockFloatApiService);

        People people = new People(Arrays.asList(persons));
        Response<People> response = Response.success(people);
        apiObservable = Observable.from(Collections.singletonList(response));
    }

    @Test
    public void givenApiReturnsPeople_whenQueryingForPeople_thenEachSinglePersonIsEmitted() {
        when(mockFloatApiService.getPeople()).thenReturn(apiObservable);

        peopleServiceClient.getPersons()
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        testSubscriber.assertReceivedOnNext(Arrays.asList(persons));
    }
}
