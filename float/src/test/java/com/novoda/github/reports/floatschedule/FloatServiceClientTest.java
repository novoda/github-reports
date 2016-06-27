package com.novoda.github.reports.floatschedule;

import com.novoda.github.reports.floatschedule.convert.FloatGithubProjectConverter;
import com.novoda.github.reports.floatschedule.convert.FloatGithubUserConverter;
import com.novoda.github.reports.floatschedule.people.PeopleServiceClient;
import com.novoda.github.reports.floatschedule.people.Person;
import com.novoda.github.reports.floatschedule.task.Task;
import com.novoda.github.reports.floatschedule.task.TaskServiceClient;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FloatServiceClientTest {

    private static final String ANY_START_DATE = "";
    private static final int ANY_NUMBER_OF_WEEKS = 0;
    private static final int ANY_PERSON_ID = 0;

    @Mock
    FloatGithubUserConverter mockFloatGithubUserConverter;

    @Mock
    FloatGithubProjectConverter mockFloatGithubProjectConverter;

    @Mock
    PeopleServiceClient mockPeopleServiceClient;

    @Mock
    TaskServiceClient mockTaskServiceClient;

    private FloatServiceClient floatServiceClient;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        floatServiceClient = new FloatServiceClient(mockFloatGithubUserConverter,
                                                    mockFloatGithubProjectConverter,
                                                    mockPeopleServiceClient,
                                                    mockTaskServiceClient);

        givenPersons();

        when(mockFloatGithubUserConverter.getGithubUser("floatUsername")).thenReturn("githubName");
        when(mockFloatGithubProjectConverter.getRepositories("floatProject")).thenReturn(Arrays.asList("repo1", "repo2"));
    }

    private void givenTasks(String startDate, int numberOfWeeks, int personId) {
        List<Task> tasks = Arrays.asList(givenATask("a"), givenATask("b"), givenATask("c"), givenATask("floatProject"));
        Observable<Task> mockTasksObservable = Observable.from(tasks);
        when(mockTaskServiceClient.getTasks(startDate, numberOfWeeks, personId)).thenReturn(mockTasksObservable);
    }

    private Task givenATask(String projectName) {
        Task aTask = mock(Task.class);
        when(aTask.getProjectName()).thenReturn(projectName);
        return aTask;
    }

    private void givenPersons() {
        List<Person> persons = Arrays.asList(givenAPerson(1, "um"), givenAPerson(2, "dois"), givenAPerson(3, "floatUsername"));
        Observable<Person> mockPersonsObservable = Observable.from(persons);
        when(mockPeopleServiceClient.getPersons()).thenReturn(mockPersonsObservable);
    }

    private Person givenAPerson(int id, String name) {
        Person aPerson = mock(Person.class);
        when(aPerson.getId()).thenReturn(id);
        when(aPerson.getName()).thenReturn(name);
        return aPerson;
    }

    // what do we want to assert?
    // . repo names are matched properly and emitted
    // . nothing emitted when there are no matches
    // . the tasks have to be in the date range provided

    @Test
    public void givenPersonsAndTask_whenGettingRepositoryNamesForFloatUser_thenTheExpectedRepositoryNamesAreEmitted() {
        givenTasks(ANY_START_DATE, ANY_NUMBER_OF_WEEKS, 3);

        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        floatServiceClient.getRepositoryNamesForFloatUser("floatUsername", ANY_START_DATE, ANY_NUMBER_OF_WEEKS)
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        testSubscriber.assertReceivedOnNext(Arrays.asList("repo1", "repo2"));
    }

}
