package com.novoda.floatschedule.task;

import com.novoda.floatschedule.convert.FloatDateConverter;
import com.novoda.floatschedule.network.FloatApiService;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import retrofit2.Response;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class TaskServiceClientTest {

    @Mock
    FloatApiService mockFloatApiService;

    @Mock
    FloatDateConverter mockFloatDateConverter;

    private TestSubscriber<Task> testSubscriber;

    private Observable<Response<Assignments>> apiObservable;

    private TaskServiceClient taskServiceClient;

    private Task[] tasks = new Task[]{
            new Task(23, "matar"),
            new Task(88, "esfolar"),
            new Task(42, "fugir")
    };

    @Before
    public void setUp() {
        initMocks(this);

        testSubscriber = new TestSubscriber<>();

        taskServiceClient = new TaskServiceClient(mockFloatApiService, mockFloatDateConverter);

        Assignment assignment = new Assignment(2014, Arrays.asList(tasks));
        Assignments assignments = new Assignments(Collections.singletonList(assignment));
        Response<Assignments> response = Response.success(assignments);
        apiObservable = Observable.from(Collections.singletonList(response));
    }

    @Test
    public void givenApiReturnsTasks_whenQueryingForTasks_thenEachSingleTaskIsEmitted() {
        when(mockFloatApiService.getTasks(anyString(), anyInt(), anyInt())).thenReturn(apiObservable);
        when(mockFloatApiService.getTasks(null, null, null)).thenReturn(apiObservable);

        taskServiceClient.getTasks()
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        testSubscriber.assertReceivedOnNext(Arrays.asList(tasks));
    }
}
