package com.novoda.floatschedule;

import com.novoda.floatschedule.convert.FailedToLoadMappingsException;
import com.novoda.floatschedule.convert.FloatDateConverter;
import com.novoda.floatschedule.convert.SheetsFloatGithubProjectConverter;
import com.novoda.floatschedule.convert.SheetsFloatGithubUserConverter;
import com.novoda.floatschedule.task.Task;
import com.novoda.floatschedule.task.TaskServiceClient;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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

    private static final Date ANY_START_DATE = Date.from(Instant.now());
    private static final TimeZone ANY_TIMEZONE = TimeZone.getTimeZone("Europe/London");
    private static final Integer ANY_NUMBER_OF_WEEKS = 40;
    private static final Integer NO_PERSON_ID = null;

    @Mock
    private FloatDateConverter floatDateConverter;

    @Mock
    private TaskServiceClient mockTaskServiceClient;

    @Mock
    private SheetsFloatGithubUserConverter mockGithubUserConverter;

    @Mock
    private SheetsFloatGithubProjectConverter mockFloatGithubProjectConverter;

    @InjectMocks
    private AssignmentServiceClient assignmentServiceClient;

    private TestSubscriber<String> testSubscriber;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(floatDateConverter.toFloatDateFormat(ANY_START_DATE, ANY_TIMEZONE)).thenReturn("2014-01-01");

        testSubscriber = new TestSubscriber<>();

        List<Task> tasks = Arrays.asList(
                givenATask("proj1: scheduled", "persA"),
                givenATask("proj1: verified", "persA"),
                givenATask("proj1: verified", "persD"),
                givenATask("proj2", "persB"),
                givenATask("proj3", "persC"),
                givenATask("proj4", "persA"),
                givenATask("proj5", "persE"));

        givenTasks(tasks);
        givenGithubUsersFor(tasks);

        when(mockFloatGithubProjectConverter.getFloatProjects("repoX")).thenReturn(Arrays.asList("proj1", "proj5"));
        when(mockFloatGithubProjectConverter.getFloatProjects("repoY")).thenReturn(Collections.singletonList("proj2"));
        when(mockFloatGithubProjectConverter.getFloatProjects("repoZ")).thenReturn(Collections.singletonList("proj3"));
        when(mockFloatGithubProjectConverter.getFloatProjects("repoK")).thenReturn(Collections.singletonList("proj1"));
    }

    private void givenTasks(List<Task> tasks) {
        Observable<Task> mockTasksObservable = Observable.from(tasks);
        when(mockTaskServiceClient.getTasks(any(Date.class), anyInt(), any(TimeZone.class), eq(NO_PERSON_ID)))
                .thenReturn(mockTasksObservable);
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
            when(mockGithubUserConverter.getGithubUser(floatUsername)).thenReturn(githubUsername);
        } catch (FailedToLoadMappingsException e) {
            // nothing
        }
    }

    @Test
    public void givenTasksAndUsers_whenGettingGithubUsernamesForARepository_thenTheCorrectUsernameIsEmitted() {

        assignmentServiceClient.getGithubUsernamesAssignedToRepositories(
                Collections.singletonList("repoZ"),
                ANY_START_DATE,
                ANY_NUMBER_OF_WEEKS,
                ANY_TIMEZONE
        )
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        testSubscriber.assertValues("persC_github");
    }

    @Test
    public void givenTasksAndUsers_whenGettingGithubUsernamesForRepositories_thenTheCorrectUsernamesAreEmittedWithoutDuplicates() {

        assignmentServiceClient.getGithubUsernamesAssignedToRepositories(
                Arrays.asList("repoX", "repoZ", "repoK"),
                ANY_START_DATE,
                ANY_NUMBER_OF_WEEKS,
                ANY_TIMEZONE
        )
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        testSubscriber.assertValues("persA_github", "persD_github", "persC_github", "persE_github");
    }

    @Test
    public void givenTasksAndUsers_whenGettingGithubUsernamesForAProject_thenTheCorrectUsernameIsEmitted() {

        assignmentServiceClient.getGithubUsernamesAssignedToProjects(
                Collections.singletonList("proj2"),
                ANY_START_DATE,
                ANY_NUMBER_OF_WEEKS,
                ANY_TIMEZONE
        )
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        testSubscriber.assertValues("persB_github");
    }

    @Test
    public void givenTasksAndUsers_whenGettingGithubUsernamesForProjects_thenTheCorrectUsernamesAreEmittedWithoutDuplicates() {

        assignmentServiceClient.getGithubUsernamesAssignedToProjects(
                Arrays.asList("proj1", "proj4"),
                ANY_START_DATE,
                ANY_NUMBER_OF_WEEKS,
                ANY_TIMEZONE
        )
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        testSubscriber.assertValues("persA_github", "persD_github");
    }

    @Test
    public void givenTasksAndUsers_whenGettingGithubUsernamesForANonExistentProject_thenTheNothingIsEmitted() {

        assignmentServiceClient.getGithubUsernamesAssignedToProjects(
                Collections.singletonList("proj88"),
                ANY_START_DATE,
                ANY_NUMBER_OF_WEEKS,
                ANY_TIMEZONE
        )
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        testSubscriber.assertNoValues();
    }
}
