package com.novoda.floatschedule;

import com.novoda.floatschedule.convert.FloatGithubUserConverter;
import com.novoda.floatschedule.task.Task;
import com.novoda.floatschedule.task.TaskServiceClient;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AssignmentServiceClientTest {

    private static final String ANY_START_DATE = "YYYY-MM-dd";
    private static final int ANY_NUMBER_OF_WEEKS = 88;
    private static final Integer NO_PERSON_ID = null;

    @Mock
    private TaskServiceClient mockTaskServiceClient;

    @Mock
    private FloatGithubUserConverter mockFloatGithubUserConverter;

    @InjectMocks
    private AssignmentServiceClient assignmentServiceClient;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        givenTasks();
        givenGithubUser("gitA", "persA");
        givenGithubUser("gitB", "persB");
        givenGithubUser("gitC", "persC");
        givenGithubUser("gitD", "persD");
    }

    private void givenTasks() {
        List<Task> tasks = Arrays.asList(givenATask("proj1", "persA"),
                                         givenATask("proj1", "persD"),
                                         givenATask("proj2", "persB"),
                                         givenATask("proj3", "persC"),
                                         givenATask("proj4", "persA"));
        Observable<Task> mockTasksObservable = Observable.from(tasks);
        when(mockTaskServiceClient.getTasks(anyString(), anyInt(), eq(NO_PERSON_ID))).thenReturn(mockTasksObservable);
    }

    private Task givenATask(String projectName, String personName) {
        Task aTask = mock(Task.class);
        when(aTask.getProjectName()).thenReturn(projectName);
        when(aTask.getPersonName()).thenReturn(personName);
        return aTask;
    }

    private void givenGithubUser(String githubUsername, String floatUsername) throws IOException {
        when(mockFloatGithubUserConverter.getGithubUser(floatUsername)).thenReturn(githubUsername);
    }

    @Test
    public void getGithubUsernamesAssignedToProjects() throws Exception {

        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        assignmentServiceClient.getGithubUsernamesAssignedToProjects(Collections.singletonList("proj2"))
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        testSubscriber.assertValues("gitB");
    }

}
