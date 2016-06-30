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

        List<Task> tasks = Arrays.asList(givenATask("proj1", "persA"), givenATask("proj1", "persD"), givenATask("proj2", "persB"),
                                         givenATask("proj3", "persC"), givenATask("proj4", "persA"));

        givenTasks(tasks);
        givenGithubUsersFor(tasks);
    }

    private void givenTasks(List<Task> tasks) {

        Observable<Task> mockTasksObservable = Observable.from(tasks);
        when(mockTaskServiceClient.getTasks(anyString(), anyInt(), eq(NO_PERSON_ID))).thenReturn(mockTasksObservable);
    }

    private Task givenATask(String projectName, String personName) {
        Task aTask = mock(Task.class);
        when(aTask.getProjectName()).thenReturn(projectName);
        when(aTask.getPersonName()).thenReturn(personName);
        return aTask;
    }

    private void givenGithubUsersFor(List<Task> tasks) {
        tasks.forEach(task -> givenGithubUser(task.getPersonName() + "_github", task.getPersonName()));
    }

    private void givenGithubUser(String githubUsername, String floatUsername) {
        try {
            when(mockFloatGithubUserConverter.getGithubUser(floatUsername)).thenReturn(githubUsername);
        } catch (IOException e) {
            // nothing
        }
    }

    @Test
    public void givenTasksAndUsers_whenGettingGithubUsernamesForAProject_thenTheCorrectUsernameIsEmitted()  {

        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        assignmentServiceClient.getGithubUsernamesAssignedToProjects(Collections.singletonList("proj2"))
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        testSubscriber.assertValues("persB_github");
    }

    @Test
    public void givenTasksAndUsers_whenGettingGithubUsernamesForProjects_thenTheCorrectUsernamesAreEmittedWithoutDuplicates() {

        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        assignmentServiceClient.getGithubUsernamesAssignedToProjects(Arrays.asList("proj1", "proj4"))
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        testSubscriber.assertValues("persA_github", "persD_github");
    }

    @Test
    public void givenTasksAndUsers_whenGettingGithubUsernamesForANonExistentProject_thenTheNothingIsEmitted() {

        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        assignmentServiceClient.getGithubUsernamesAssignedToProjects(Collections.singletonList("proj88"))
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        testSubscriber.assertNoValues();
    }
}
