package com.novoda.github.reports.floatschedule.project;

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

public class ProjectServiceClientTest {

    @Mock
    FloatApiService mockFloatApiService;

    private TestSubscriber<Project> testSubscriber;

    private Observable<Response<Projects>> apiObservable;

    private ProjectServiceClient projectServiceClient;

    private Project[] projects = new Project[] {
            new Project(23, "pirata"),
            new Project(88, "panados"),
            new Project(42, "pão")
    };

    @Before
    public void setUp() {
        initMocks(this);

        testSubscriber = new TestSubscriber<>();

        projectServiceClient = new ProjectServiceClient(mockFloatApiService);

        Projects projectsContainer = new Projects(Arrays.asList(projects));
        Response<Projects> response = Response.success(projectsContainer);
        apiObservable = Observable.from(Collections.singletonList(response));
    }

    @Test
    public void givenApiReturnsProjects_whenQueryingForProjects_thenEachSingleProjectIsEmitted() {
        when(mockFloatApiService.getProjects()).thenReturn(apiObservable);

        projectServiceClient.getProjects()
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        testSubscriber.assertReceivedOnNext(Arrays.asList(projects));
    }
}
