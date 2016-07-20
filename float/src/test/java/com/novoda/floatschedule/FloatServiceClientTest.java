package com.novoda.floatschedule;

import com.novoda.floatschedule.convert.*;
import com.novoda.floatschedule.people.PeopleServiceClient;
import com.novoda.floatschedule.people.Person;
import com.novoda.floatschedule.task.Task;
import com.novoda.floatschedule.task.TaskServiceClient;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.*;
import static org.mockito.MockitoAnnotations.initMocks;

@SuppressWarnings("Duplicates")
public class FloatServiceClientTest {

    private static final Date ANY_START_DATE = Date.from(Instant.now());
    private static final Date ANY_END_DATE = Date.from(Instant.now().plus(Duration.ofDays(8)));
    private static final int ANY_NUMBER_OF_WEEKS = 42;
    private static final int ANY_PERSON_ID = 23;

    private static final String ANY_FLOAT_USERNAME = "imbecil";
    private static final String ANY_OTHER_FLOAT_USERNAME = "outro palerma";
    private static final String YET_ANOTHER_FLOAT_USERNAME = "camelo";

    private static final String ANY_GITHUB_USERNAME = "palerma";
    private static final String ANY_OTHER_GITHUB_USERNAME = "opalerma";
    private static final String YET_ANOTHER_GITHUB_USERNAME = "camelino";

    private static final String ANY_FLOAT_PROJECT_NAME = "palerma";
    private static final String ANY_GITHUB_REPO_NAME = "lixo";
    private static final String ANY_OTHER_GITHUB_REPO_NAME = "nojo";

    private static final Person ANY_PERSON = givenAPerson(ANY_PERSON_ID, ANY_FLOAT_USERNAME);
    private static final Person ANY_OTHER_PERSON = givenAPerson(13, ANY_OTHER_FLOAT_USERNAME);
    private static final Person YET_ANOTHER_PERSON = givenAPerson(74, YET_ANOTHER_FLOAT_USERNAME);

    private static final Task ANY_TASK = givenATask("get down", ANY_FLOAT_PROJECT_NAME, ANY_PERSON);
    private static final Task ANY_OTHER_TASK = givenATask("party", "xxx", ANY_OTHER_PERSON);
    private static final Task YET_ANOTHER_TASK = givenATask("have cereal", "xxx", YET_ANOTHER_PERSON);
    private static final Task ONE_MORE_TASK = givenATask("and party", "another float project", ANY_PERSON);
    private static final Task HOLIDAYS_TASK = givenATask("Holidays", "Holiday / Sick Leave", ANY_PERSON);

    private static final List<Task> ANY_TASKS = Arrays.asList(ANY_TASK, ANY_OTHER_TASK, YET_ANOTHER_TASK, ONE_MORE_TASK, HOLIDAYS_TASK);

    @Mock
    private FloatGithubUserConverter mockFloatGithubUserConverter;

    @Mock
    private FloatGithubProjectConverter mockFloatGithubProjectConverter;

    @Mock
    private PeopleServiceClient mockPeopleServiceClient;

    @Mock
    private TaskServiceClient mockTaskServiceClient;

    @Mock
    private NumberOfWeeksCalculator mockNumberOfWeeksCalculator;

    @InjectMocks
    private FloatServiceClient floatServiceClient;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        givenPersons();

        setUserConversion(ANY_FLOAT_USERNAME, ANY_GITHUB_USERNAME);
        setUserConversion(ANY_OTHER_FLOAT_USERNAME, ANY_OTHER_GITHUB_USERNAME);
        setUserConversion(YET_ANOTHER_FLOAT_USERNAME, YET_ANOTHER_GITHUB_USERNAME);

        given(mockFloatGithubProjectConverter.getRepositories(ANY_FLOAT_PROJECT_NAME))
                .willReturn(Arrays.asList(ANY_GITHUB_REPO_NAME, ANY_OTHER_GITHUB_REPO_NAME));

        given(mockNumberOfWeeksCalculator.getNumberOfWeeksOrNullIn(ANY_START_DATE, ANY_END_DATE))
                .willReturn(ANY_NUMBER_OF_WEEKS);
    }

    private void setUserConversion(String floatUsername, String githubUsername) throws IOException {
        given(mockFloatGithubUserConverter.getGithubUser(floatUsername)).willReturn(githubUsername);
        given(mockFloatGithubUserConverter.getFloatUser(githubUsername)).willReturn(floatUsername);
    }

    private static Task givenATask(String taskName, String projectName, Person person) {
        return new Task(taskName, person.getName(), projectName);
    }

    private void givenPersons() {
        List<Person> persons = Arrays.asList(ANY_PERSON, ANY_OTHER_PERSON, YET_ANOTHER_PERSON);
        Observable<Person> mockPersonsObservable = Observable.from(persons);
        given(mockPeopleServiceClient.getPersons()).willReturn(mockPersonsObservable);
    }

    private static Person givenAPerson(int id, String name) {
        return new Person(id, name);
    }

    private void givenTasks(List<Task> tasks) {
        Observable<Task> mockTasksObservable = Observable.from(tasks);
        given(mockTaskServiceClient.getTasks(any(Date.class), anyInt(), anyInt())).willReturn(mockTasksObservable);
    }

    private void givenTasks(List<Task> tasks, Person person) {
        Observable<Task> mockTasksObservable = Observable.from(tasks);
        given(
                mockTaskServiceClient.getTasks(
                        any(Date.class),
                        anyInt(),
                        eq(person.getId())
                )
        ).willReturn(mockTasksObservable);
    }

    @Test
    public void givenPersonsAndTasks_whenGettingRepositoryNamesForFloatUser_thenTheExpectedRepositoryNamesAreEmitted() {
        givenTasks(ANY_TASKS);

        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        floatServiceClient.getRepositoryNamesForFloatUser(ANY_FLOAT_USERNAME, ANY_START_DATE, ANY_NUMBER_OF_WEEKS)
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        testSubscriber.assertReceivedOnNext(Arrays.asList(ANY_GITHUB_REPO_NAME, ANY_OTHER_GITHUB_REPO_NAME));
    }

    @Test
    public void givenPersonsAndTasks_whenGettingRepositoryNamesForGithubUser_thenTheExpectedRepositoryNamesAreEmitted()
            throws IOException, NoMatchFoundException {

        givenTasks(ANY_TASKS);

        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        floatServiceClient.getRepositoryNamesForGithubUser(ANY_GITHUB_USERNAME, ANY_START_DATE, ANY_NUMBER_OF_WEEKS)
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        testSubscriber.assertReceivedOnNext(Arrays.asList(ANY_GITHUB_REPO_NAME, ANY_OTHER_GITHUB_REPO_NAME));
    }

    @Test
    public void givenPersonsAndTasks_whenGettingRepositoryNamesForGithubUserWithNoTasksAssigned_thenNoItemsAreEmitted() {
        Observable<Task> mockTasksObservable = Observable.from(Collections.emptyList());
        given(mockTaskServiceClient.getTasks(any(Date.class), anyInt(), anyInt())).willReturn(mockTasksObservable);

        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        floatServiceClient.getRepositoryNamesForFloatUser(ANY_FLOAT_USERNAME, ANY_START_DATE, ANY_NUMBER_OF_WEEKS)
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        testSubscriber.assertNoValues();
    }

    @Test
    public void givenPersonsAndTasks_whenGettingTasksForFloatUser_thenWeOnlyGetTasksForThatUser() throws Exception {
        givenTasks(Arrays.asList(ANY_TASK, ONE_MORE_TASK));

        TestSubscriber<Task> testSubscriber = new TestSubscriber<>();
        floatServiceClient.getTasksForFloatUser(ANY_FLOAT_USERNAME, ANY_START_DATE, ANY_NUMBER_OF_WEEKS)
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);
        List<Task> actualTasks = testSubscriber.getOnNextEvents();

        assertTasksAreExpected(Arrays.asList(ANY_TASK, ONE_MORE_TASK), actualTasks);
    }

    @Test
    public void givenPersonsAndTasks_whenGettingTasksForFloatUser_thenWeOnlyGetTasksThatAreNotHolidays()
            throws Exception {

        givenTasks(ANY_TASKS);

        TestSubscriber<Task> testSubscriber = new TestSubscriber<>();
        floatServiceClient.getTasksForFloatUser(ANY_FLOAT_USERNAME, ANY_START_DATE, ANY_NUMBER_OF_WEEKS)
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);
        List<Task> actualTasks = testSubscriber.getOnNextEvents();

        assertTasksDoNotHaveHolidays(actualTasks);
    }

    @Test
    public void givenPersonsAndTasks_whenGettingTasksForGithubUser_thenWeOnlyGetTasksForCorrespondingFloatUser()
            throws Exception {

        givenTasks(Arrays.asList(ANY_TASK, ONE_MORE_TASK));

        TestSubscriber<Task> testSubscriber = new TestSubscriber<>();
        floatServiceClient.getTasksForGithubUser(ANY_GITHUB_USERNAME, ANY_START_DATE, ANY_NUMBER_OF_WEEKS)
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);
        List<Task> actualTasks = testSubscriber.getOnNextEvents();

        assertTasksAreExpected(Arrays.asList(ANY_TASK, ONE_MORE_TASK), actualTasks);
    }

    @Test
    public void givenPersonsAndTasks_whenGettingTasksForMultipleGithubUsers_thenWeGetTasksMappedToGithubUsers() {
        givenTasks(Arrays.asList(ANY_TASK, ONE_MORE_TASK), ANY_PERSON);
        givenTasks(Collections.singletonList(YET_ANOTHER_TASK), YET_ANOTHER_PERSON);

        TestSubscriber<Map.Entry<String, List<Task>>> testSubscriber = new TestSubscriber<>();
        List<String> someGithubUsers = Arrays.asList(ANY_GITHUB_USERNAME, YET_ANOTHER_GITHUB_USERNAME);
        floatServiceClient.getTasksForGithubUsers(someGithubUsers, ANY_START_DATE, ANY_END_DATE)
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        List<Map.Entry<String, List<Task>>> expectedTasks = Arrays.asList(
                new AbstractMap.SimpleImmutableEntry<>(ANY_GITHUB_USERNAME, Arrays.asList(ANY_TASK, ONE_MORE_TASK)),
                new AbstractMap.SimpleImmutableEntry<>(YET_ANOTHER_GITHUB_USERNAME, Collections.singletonList(YET_ANOTHER_TASK))
        );
        testSubscriber.assertReceivedOnNext(expectedTasks);
    }

    @Test
    public void givenPersonsAndTasksAndFailingFloatUsernameLookup_whenGettingTasksForGithubUser_thenWeGetErroringObservable()
            throws IOException {

        given(mockFloatGithubUserConverter.getFloatUser(ANY_GITHUB_USERNAME)).willThrow(IOException.class);

        TestSubscriber<Task> testSubscriber = new TestSubscriber<>();
        floatServiceClient.getTasksForGithubUser(ANY_GITHUB_USERNAME, ANY_START_DATE, ANY_NUMBER_OF_WEEKS)
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        testSubscriber.assertError(IOException.class);
    }

    @Test
    public void givenListOfGithubUsernames_whenConvertToFloatUsernames_thenReturnMatchingFloatUsernames() {
        List<String> githubUsernames = Arrays.asList(ANY_GITHUB_USERNAME, YET_ANOTHER_GITHUB_USERNAME);

        Map<String, String> actual = floatServiceClient.mapFloatToGithubUsernames(githubUsernames);

        Map<String, String> expected = new HashMap<>();
        expected.put(ANY_FLOAT_USERNAME, ANY_GITHUB_USERNAME);
        expected.put(YET_ANOTHER_FLOAT_USERNAME, YET_ANOTHER_GITHUB_USERNAME);
        assertEquals(expected, actual);
    }

    @Test
    public void givenListOfGithubUsernamesWithUsernameNotMatchedInFloat_whenConvertToFloatUsernames_thenThrowGithubToFloatUserMatchNotFoundException()
            throws IOException {

        String nonExistingGithubUsername = "this-user-does-not-exist-on-float";
        given(mockFloatGithubUserConverter.getFloatUser(nonExistingGithubUsername)).willThrow(IOException.class);
        List<String> githubUsernames = Arrays.asList(ANY_GITHUB_USERNAME, YET_ANOTHER_GITHUB_USERNAME, nonExistingGithubUsername);

        expectedException.expect(GithubToFloatUserMatchNotFoundException.class);
        floatServiceClient.mapFloatToGithubUsernames(githubUsernames);
    }

    private void assertTasksDoNotHaveHolidays(List<Task> actualTasks) {
        List<Task> expectedTasks = ANY_TASKS
                .stream()
                .filter(task -> !task.getName().contains("Holiday"))
                .collect(Collectors.toList());
        assertTasksAreExpected(expectedTasks, actualTasks);
    }

    private void assertTasksAreExpected(List<Task> expectedTasks, List<Task> actualTasks) {
        assertEquals(expectedTasks.size(), actualTasks.size());
        for (int i = 0; i < expectedTasks.size(); i++) {
            assertEquals(expectedTasks.get(i).getName(), actualTasks.get(i).getName());
        }
    }
}
