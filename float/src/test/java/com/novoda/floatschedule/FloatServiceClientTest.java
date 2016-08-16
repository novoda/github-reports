package com.novoda.floatschedule;

import com.novoda.floatschedule.convert.*;
import com.novoda.floatschedule.people.PeopleServiceClient;
import com.novoda.floatschedule.people.Person;
import com.novoda.floatschedule.task.Task;
import com.novoda.floatschedule.task.TaskServiceClient;
import com.novoda.github.reports.data.model.UserAssignments;
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

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static junit.framework.TestCase.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.*;
import static org.mockito.MockitoAnnotations.initMocks;

@SuppressWarnings("Duplicates")
public class FloatServiceClientTest {

    private static final Date ANY_START_DATE = new GregorianCalendar(2016, 6, 27).getTime();
    private static final Date ANY_END_DATE = Date.from(Instant.ofEpochMilli(ANY_START_DATE.getTime())
            .plus(Duration.ofDays(8)));
    private static final String ANY_FLOAT_START_DATE = "2016-07-27";
    private static final String ANY_FLOAT_END_DATE = "2016-08-04";

    private static final int ANY_NUMBER_OF_WEEKS = 42;
    private static final int ANY_PERSON_ID = 23;

    private static final String ANY_FLOAT_USERNAME = "Super Mario";
    private static final String ANY_OTHER_FLOAT_USERNAME = "Super Luigi";
    private static final String YET_ANOTHER_FLOAT_USERNAME = "Princess Peach";

    private static final String ANY_GITHUB_USERNAME = "mario";
    private static final String ANY_OTHER_GITHUB_USERNAME = "luigi";
    private static final String YET_ANOTHER_GITHUB_USERNAME = "peach";

    private static final String ANY_FLOAT_PROJECT_NAME = "All-4";
    private static final String ANY_OTHER_FLOAT_PROJECT_NAME = "Project X";
    private static final String YET_ANOTHER_FLOAT_PROJECT_NAME = "Novoda BBQs";
    private static final String ANY_OTHER_GITHUB_REPO_NAME = "all-4-android-tv";

    private static final String ANY_GITHUB_REPO_NAME = "all-4";
    private static final Person ANY_PERSON = givenAPerson(ANY_PERSON_ID, ANY_FLOAT_USERNAME);
    private static final Person ANY_OTHER_PERSON = givenAPerson(13, ANY_OTHER_FLOAT_USERNAME);

    private static final Person YET_ANOTHER_PERSON = givenAPerson(74, YET_ANOTHER_FLOAT_USERNAME);
    private static final Task ANY_TASK = givenATask("All-4 Verified", ANY_FLOAT_PROJECT_NAME, ANY_PERSON);
    private static final Task ANY_OTHER_TASK = givenATask("Do something secret", ANY_OTHER_FLOAT_PROJECT_NAME, ANY_OTHER_PERSON);
    private static final Task YET_ANOTHER_TASK = givenATask("Don't tell anyone", ANY_OTHER_FLOAT_PROJECT_NAME, YET_ANOTHER_PERSON);
    private static final Task ONE_MORE_TASK = givenATask("Party hard", YET_ANOTHER_FLOAT_PROJECT_NAME, ANY_PERSON);
    private static final Task HOLIDAYS_TASK = givenATask("Holidays", "Holiday / Sick Leave", ANY_PERSON);

    private static final List<Task> ANY_TASKS = asList(
            ANY_TASK,
            ANY_OTHER_TASK,
            YET_ANOTHER_TASK,
            ONE_MORE_TASK,
            HOLIDAYS_TASK
    );

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

    @Mock
    private FloatDateConverter floatDateConverter;

    @InjectMocks
    private FloatServiceClient floatServiceClient;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static Person givenAPerson(int id, String name) {
        return new Person(id, name);
    }

    private static Task givenATask(String taskName, String projectName, Person person) {
        return new Task(taskName, person.getName(), projectName);
    }

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        givenPersons();

        setUserConversion(ANY_FLOAT_USERNAME, ANY_GITHUB_USERNAME);
        setUserConversion(ANY_OTHER_FLOAT_USERNAME, ANY_OTHER_GITHUB_USERNAME);
        setUserConversion(YET_ANOTHER_FLOAT_USERNAME, YET_ANOTHER_GITHUB_USERNAME);

        given(mockFloatGithubProjectConverter.getRepositories(ANY_FLOAT_PROJECT_NAME))
                .willReturn(asList(ANY_GITHUB_REPO_NAME, ANY_OTHER_GITHUB_REPO_NAME));

        given(mockNumberOfWeeksCalculator.getNumberOfWeeksOrNullIn(ANY_START_DATE, ANY_END_DATE))
                .willReturn(ANY_NUMBER_OF_WEEKS);

        given(floatDateConverter.fromFloatDateFormatOrNoDate(ANY_FLOAT_START_DATE)).willReturn(ANY_START_DATE);
        given(floatDateConverter.fromFloatDateFormatOrNoDate(ANY_FLOAT_END_DATE)).willReturn(ANY_END_DATE);
        given(floatDateConverter.toFloatDateFormat(ANY_START_DATE)).willReturn(ANY_FLOAT_START_DATE);
        given(floatDateConverter.toFloatDateFormat(ANY_END_DATE)).willReturn(ANY_FLOAT_END_DATE);

        given(mockFloatGithubUserConverter.getGithubUsers()).willReturn(asList(
                ANY_GITHUB_USERNAME,
                ANY_OTHER_GITHUB_USERNAME,
                YET_ANOTHER_GITHUB_USERNAME
        ));
    }

    private void givenPersons() {
        List<Person> persons = asList(ANY_PERSON, ANY_OTHER_PERSON, YET_ANOTHER_PERSON);
        Observable<Person> mockPersonsObservable = Observable.from(persons);
        given(mockPeopleServiceClient.getPersons()).willReturn(mockPersonsObservable);
    }

    private void setUserConversion(String floatUsername, String githubUsername) throws IOException {
        given(mockFloatGithubUserConverter.getGithubUser(floatUsername)).willReturn(githubUsername);
        given(mockFloatGithubUserConverter.getFloatUser(githubUsername)).willReturn(floatUsername);
    }

    @Test
    public void givenPersonsAndTasks_whenGettingRepositoryNamesForFloatUser_thenTheExpectedRepositoryNamesAreEmitted() {
        givenTasks(ANY_TASKS);

        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        floatServiceClient.getRepositoryNamesForFloatUser(ANY_FLOAT_USERNAME, ANY_START_DATE, ANY_NUMBER_OF_WEEKS)
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        testSubscriber.assertReceivedOnNext(asList(ANY_GITHUB_REPO_NAME, ANY_OTHER_GITHUB_REPO_NAME));
    }

    @Test
    public void givenPersonsAndTasks_whenGettingRepositoryNamesForGithubUser_thenTheExpectedRepositoryNamesAreEmitted()
            throws IOException, NoMatchFoundException {

        givenTasks(ANY_TASKS);

        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        floatServiceClient.getRepositoryNamesForGithubUser(ANY_GITHUB_USERNAME, ANY_START_DATE, ANY_NUMBER_OF_WEEKS)
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        testSubscriber.assertReceivedOnNext(asList(ANY_GITHUB_REPO_NAME, ANY_OTHER_GITHUB_REPO_NAME));
    }

    @Test
    public void givenPersonsAndTasks_whenGettingRepositoryNamesForGithubUserWithNoTasksAssigned_thenNoItemsAreEmitted() {
        Observable<Task> mockTasksObservable = Observable.from(emptyList());
        given(mockTaskServiceClient.getTasks(any(Date.class), anyInt(), anyInt())).willReturn(mockTasksObservable);

        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        floatServiceClient.getRepositoryNamesForFloatUser(ANY_FLOAT_USERNAME, ANY_START_DATE, ANY_NUMBER_OF_WEEKS)
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        testSubscriber.assertNoValues();
    }

    @Test
    public void givenPersonsAndTasks_whenGettingTasksForFloatUser_thenWeOnlyGetTasksForThatUser() throws Exception {
        givenTasks(asList(ANY_TASK, ONE_MORE_TASK));

        TestSubscriber<Task> testSubscriber = new TestSubscriber<>();
        floatServiceClient.getTasksForFloatUser(ANY_FLOAT_USERNAME, ANY_START_DATE, ANY_NUMBER_OF_WEEKS)
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);
        List<Task> actualTasks = testSubscriber.getOnNextEvents();

        assertTasksAreExpected(asList(ANY_TASK, ONE_MORE_TASK), actualTasks);
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

    private void assertTasksDoNotHaveHolidays(List<Task> actualTasks) {
        List<Task> expectedTasks = ANY_TASKS
                .stream()
                .filter(task -> !task.getName().contains("Holiday"))
                .collect(Collectors.toList());
        assertTasksAreExpected(expectedTasks, actualTasks);
    }

    @Test
    public void givenPersonsAndTasks_whenGettingTasksForGithubUser_thenWeOnlyGetTasksForCorrespondingFloatUser()
            throws Exception {

        givenTasks(asList(ANY_TASK, ONE_MORE_TASK));

        TestSubscriber<Task> testSubscriber = new TestSubscriber<>();
        floatServiceClient.getTasksForGithubUser(ANY_GITHUB_USERNAME, ANY_START_DATE, ANY_NUMBER_OF_WEEKS)
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);
        List<Task> actualTasks = testSubscriber.getOnNextEvents();

        assertTasksAreExpected(asList(ANY_TASK, ONE_MORE_TASK), actualTasks);
    }

    private void givenTasks(List<Task> tasks) {
        Observable<Task> mockTasksObservable = Observable.from(tasks);
        given(mockTaskServiceClient.getTasks(any(Date.class), anyInt(), anyInt())).willReturn(mockTasksObservable);
    }

    private void assertTasksAreExpected(List<Task> expectedTasks, List<Task> actualTasks) {
        assertEquals(expectedTasks.size(), actualTasks.size());
        for (int i = 0; i < expectedTasks.size(); i++) {
            assertEquals(expectedTasks.get(i).getName(), actualTasks.get(i).getName());
        }
    }

    @Test
    public void givenPersonsAndTasks_whenGettingTasksForMultipleGithubUsers_thenWeGetTasksMappedToGithubUsers() {
        givenTasksForPerson(asList(ANY_TASK, ONE_MORE_TASK), ANY_PERSON);
        givenTasksForPerson(singletonList(YET_ANOTHER_TASK), YET_ANOTHER_PERSON);

        TestSubscriber<Map.Entry<String, List<Task>>> testSubscriber = new TestSubscriber<>();
        List<String> someGithubUsers = asList(ANY_GITHUB_USERNAME, YET_ANOTHER_GITHUB_USERNAME);
        floatServiceClient.getTasksForGithubUsers(someGithubUsers, ANY_START_DATE, ANY_END_DATE)
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        List<Map.Entry<String, List<Task>>> expectedTasks = asList(
                new AbstractMap.SimpleImmutableEntry<>(ANY_GITHUB_USERNAME, asList(ANY_TASK, ONE_MORE_TASK)),
                new AbstractMap.SimpleImmutableEntry<>(YET_ANOTHER_GITHUB_USERNAME, singletonList(YET_ANOTHER_TASK))
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
        List<String> githubUsernames = asList(ANY_GITHUB_USERNAME, YET_ANOTHER_GITHUB_USERNAME);

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
        List<String> githubUsernames = asList(ANY_GITHUB_USERNAME, YET_ANOTHER_GITHUB_USERNAME, nonExistingGithubUsername);

        expectedException.expect(GithubToFloatUserMatchNotFoundException.class);
        floatServiceClient.mapFloatToGithubUsernames(githubUsernames);
    }

    @Test
    public void givenListOfGithubUsernamesWithTasks_whenGetGithubUsersAssignmentsInDateRange_thenReturnMapOfUserAssignments() {
        List<String> githubUsernames = asList(ANY_GITHUB_USERNAME, YET_ANOTHER_GITHUB_USERNAME);
        givenTasksForPerson(asList(ANY_TASK, ONE_MORE_TASK), ANY_PERSON);
        givenTasksForPerson(singletonList(YET_ANOTHER_TASK), YET_ANOTHER_PERSON);

        Map<String, List<UserAssignments>> actual = floatServiceClient.getGithubUsersAssignmentsInDateRange(
                githubUsernames,
                ANY_START_DATE,
                ANY_END_DATE
        );

        Map<String, List<UserAssignments>> expected = new HashMap<>();
        expected.put(ANY_GITHUB_USERNAME, asList(
                UserAssignments.builder()
                        .assignedProject(ANY_FLOAT_PROJECT_NAME)
                        .assignedRepositories(asList(ANY_GITHUB_REPO_NAME, ANY_OTHER_GITHUB_REPO_NAME))
                        .build(),
                UserAssignments.builder()
                        .assignedProject(YET_ANOTHER_FLOAT_PROJECT_NAME)
                        .assignedRepositories(emptyList())
                        .build()
        ));
        expected.put(YET_ANOTHER_GITHUB_USERNAME, singletonList(
                UserAssignments.builder()
                        .assignedProject(ANY_OTHER_FLOAT_PROJECT_NAME)
                        .assignedRepositories(emptyList())
                        .build()
        ));

        assertEquals(expected, actual);
    }

    @Test
    public void givenEmptyListOfGithubUsernamesWithTasks_whenGetGithubUsersAssignmentsInDateRange_thenReturnMapOfAllGithubUsers() {
        List<String> emptyGithubUsernames = emptyList();
        givenTasksForAllPersons();

        Map<String, List<UserAssignments>> actualMap = floatServiceClient.getGithubUsersAssignmentsInDateRange(
                emptyGithubUsernames,
                ANY_START_DATE,
                ANY_END_DATE
        );

        assertAllPersonsInKeySet(actualMap.keySet());
    }

    @Test
    public void givenNullListOfGithubUsernamesWithTasks_whenGetGithubUsersAssignmentsInDateRange_thenReturnMapOfAllGithubUsers() {
        List<String> nullGithubUsernames = null;
        givenTasksForAllPersons();

        Map<String, List<UserAssignments>> actualMap = floatServiceClient.getGithubUsersAssignmentsInDateRange(
                nullGithubUsernames,
                ANY_START_DATE,
                ANY_END_DATE
        );

        assertAllPersonsInKeySet(actualMap.keySet());
    }

    private void givenTasksForAllPersons() {
        givenTasksForPerson(asList(ANY_TASK, ONE_MORE_TASK), ANY_PERSON);
        givenTasksForPerson(emptyList(), ANY_OTHER_PERSON);
        givenTasksForPerson(singletonList(YET_ANOTHER_TASK), YET_ANOTHER_PERSON);
    }

    private void givenTasksForPerson(List<Task> tasks, Person person) {
        Observable<Task> mockTasksObservable = Observable.from(tasks);
        given(
                mockTaskServiceClient.getTasks(
                        any(Date.class),
                        anyInt(),
                        eq(person.getId())
                )
        ).willReturn(mockTasksObservable);
    }

    private void assertAllPersonsInKeySet(Set<String> actual) {
        assertEquals(new HashSet<>(asList(
                ANY_GITHUB_USERNAME,
                ANY_OTHER_GITHUB_USERNAME,
                YET_ANOTHER_GITHUB_USERNAME
        )), actual);
    }

}
