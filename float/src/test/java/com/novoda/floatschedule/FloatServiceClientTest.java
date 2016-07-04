package com.novoda.floatschedule;

import com.novoda.floatschedule.convert.FloatGithubProjectConverter;
import com.novoda.floatschedule.convert.FloatGithubUserConverter;
import com.novoda.floatschedule.convert.NoMatchFoundException;
import com.novoda.floatschedule.people.PeopleServiceClient;
import com.novoda.floatschedule.people.Person;
import com.novoda.floatschedule.task.Task;
import com.novoda.floatschedule.task.TaskServiceClient;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FloatServiceClientTest {

    private static final Date ANY_START_DATE = Date.from(Instant.now());
    private static final int ANY_NUMBER_OF_WEEKS = 42;
    private static final int ANY_PERSON_ID = 23;
    private static final String ANY_FLOAT_USERNAME = "imbecil";
    private static final String ANY_GITHUB_USERNAME = "palerma";
    private static final String ANY_FLOAT_PROJECT_NAME = "palerma";
    private static final String ANY_GITHUB_REPO_NAME = "lixo";
    private static final String ANY_OTHER_GITHUB_REPO_NAME = "nojo";

    @Mock
    private FloatGithubUserConverter mockFloatGithubUserConverter;

    @Mock
    private FloatGithubProjectConverter mockFloatGithubProjectConverter;

    @Mock
    private PeopleServiceClient mockPeopleServiceClient;

    @Mock
    private TaskServiceClient mockTaskServiceClient;

    @InjectMocks
    private FloatServiceClient floatServiceClient;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        givenPersons();

        when(mockFloatGithubUserConverter.getGithubUser(ANY_FLOAT_USERNAME)).thenReturn(ANY_GITHUB_USERNAME);
        when(mockFloatGithubUserConverter.getFloatUser(ANY_GITHUB_USERNAME)).thenReturn(ANY_FLOAT_USERNAME);
        when(mockFloatGithubProjectConverter.getRepositories(ANY_FLOAT_PROJECT_NAME)).thenReturn(Arrays.asList(ANY_GITHUB_REPO_NAME,
                                                                                                               ANY_OTHER_GITHUB_REPO_NAME));
    }

    private void givenTasks(Date startDate, int numberOfWeeks, int personId) {
        List<Task> tasks = Arrays.asList(givenATask("a"), givenATask("b"), givenATask("c"), givenATask(ANY_FLOAT_PROJECT_NAME));
        Observable<Task> mockTasksObservable = Observable.from(tasks);
        when(mockTaskServiceClient.getTasks(startDate, numberOfWeeks, personId)).thenReturn(mockTasksObservable);
    }

    private Task givenATask(String projectName) {
        Task aTask = mock(Task.class);
        when(aTask.getProjectName()).thenReturn(projectName);
        return aTask;
    }

    private void givenPersons() {
        List<Person> persons = Arrays.asList(givenAPerson(1, "um"), givenAPerson(2, "dois"), givenAPerson(ANY_PERSON_ID, ANY_FLOAT_USERNAME));
        Observable<Person> mockPersonsObservable = Observable.from(persons);
        when(mockPeopleServiceClient.getPersons()).thenReturn(mockPersonsObservable);
    }

    private Person givenAPerson(int id, String name) {
        Person aPerson = mock(Person.class);
        when(aPerson.getId()).thenReturn(id);
        when(aPerson.getName()).thenReturn(name);
        return aPerson;
    }

    @Test
    public void givenPersonsAndTasks_whenGettingRepositoryNamesForFloatUser_thenTheExpectedRepositoryNamesAreEmitted() {
        givenTasks(ANY_START_DATE, ANY_NUMBER_OF_WEEKS, ANY_PERSON_ID);

        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        floatServiceClient.getRepositoryNamesForFloatUser(ANY_FLOAT_USERNAME, ANY_START_DATE, ANY_NUMBER_OF_WEEKS)
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        testSubscriber.assertReceivedOnNext(Arrays.asList(ANY_GITHUB_REPO_NAME, ANY_OTHER_GITHUB_REPO_NAME));
    }

    @Test
    public void givenPersonsAndTasks_whenGettingRepositoryNamesForGithubUserWithNoTasksAssigned_thenNoItemsAreEmitted() {
        Observable<Task> mockTasksObservable = Observable.from(Collections.emptyList());
        when(mockTaskServiceClient.getTasks(any(Date.class), anyInt(), anyInt())).thenReturn(mockTasksObservable);

        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        floatServiceClient.getRepositoryNamesForFloatUser(ANY_FLOAT_USERNAME, ANY_START_DATE, ANY_NUMBER_OF_WEEKS)
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        testSubscriber.assertNoValues();
    }

    @Test
    public void givenPersonsAndTasks_whenGettingRepositoryNamesForGithubUser_thenTheExpectedRepositoryNamesAreEmitted()
            throws IOException, NoMatchFoundException {

        givenTasks(ANY_START_DATE, ANY_NUMBER_OF_WEEKS, ANY_PERSON_ID);

        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        floatServiceClient.getRepositoryNamesForGithubUser(ANY_GITHUB_USERNAME, ANY_START_DATE, ANY_NUMBER_OF_WEEKS)
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        testSubscriber.assertReceivedOnNext(Arrays.asList(ANY_GITHUB_REPO_NAME, ANY_OTHER_GITHUB_REPO_NAME));
    }
}
