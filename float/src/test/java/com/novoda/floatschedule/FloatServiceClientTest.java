package com.novoda.floatschedule;

import com.novoda.floatschedule.convert.FailedToLoadMappingsException;
import com.novoda.floatschedule.convert.FloatDateConverter;
import com.novoda.floatschedule.convert.FloatGithubProjectConverter;
import com.novoda.floatschedule.convert.GithubToFloatUserMatchNotFoundException;
import com.novoda.floatschedule.convert.GithubUserConverter;
import com.novoda.floatschedule.convert.NoMatchFoundException;
import com.novoda.floatschedule.convert.NumberOfWeeksCalculator;
import com.novoda.floatschedule.people.PeopleServiceClient;
import com.novoda.floatschedule.people.Person;
import com.novoda.floatschedule.task.Task;
import com.novoda.floatschedule.task.TaskServiceClient;
import com.novoda.github.reports.data.model.UserAssignments;

import java.time.Duration;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import rx.Observable;
import rx.functions.Action1;

import static com.novoda.floatschedule.TestSubscriberAssert.assertThatAnObservable;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static junit.framework.TestCase.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.MockitoAnnotations.initMocks;

@SuppressWarnings({"Duplicates", "ArraysAsListWithZeroOrOneArgument", "ConstantConditions"})
public class FloatServiceClientTest {

    private static final Date ANY_START_DATE = new GregorianCalendar(2016, 6, 27).getTime();
    private static final Date ANY_END_DATE = Date.from(Instant.ofEpochMilli(ANY_START_DATE.getTime()).plus(Duration.ofDays(8)));
    private static final String ANY_FLOAT_START_DATE = "2016-07-27";
    private static final String ANY_FLOAT_END_DATE = "2016-08-04";
    private static final TimeZone ANY_TIMEZONE = TimeZone.getTimeZone("Europe/London");
    private static final int ANY_NUMBER_OF_WEEKS = 42;

    private static final String FLOAT_MARIO = "Super Mario";
    private static final String FLOAT_LUIGI = "Super Luigi";
    private static final String FLOAT_PEACH = "Princess Peach";

    private static final String GITHUB_MARIO = "mario";
    private static final String GITHUB_LUIGI = "luigi";
    private static final String GITHUB_PEACH = "peach";

    private static final Person PERSON_MARIO = givenAPerson(23, FLOAT_MARIO);
    private static final Person PERSON_LUIGI = givenAPerson(13, FLOAT_LUIGI);
    private static final Person PERSON_PEACH = givenAPerson(74, FLOAT_PEACH);

    private static final String FLOAT_PROJECT_ALL_4 = "All-4";
    private static final String FLOAT_PROJECT_X = "Project X";
    private static final String FLOAT_PROJECT_BBQS = "Novoda BBQs";
    private static final String FLOAT_PROJECT_NOVODA_TV = "all-4-android-tv";

    private static final String GITHUB_ALL_4 = "all-4";

    private static final Task TASK_MARIO_ALL_4 = givenATask("All-4 Verified", FLOAT_PROJECT_ALL_4, PERSON_MARIO);
    private static final Task TASK_LUIGI_X = givenATask("Do something secret", FLOAT_PROJECT_X, PERSON_LUIGI);
    private static final Task TASK_PEACH_X = givenATask("Don't tell anyone", FLOAT_PROJECT_X, PERSON_PEACH);
    private static final Task TASK_MARIO_BBQ = givenATask("Party hard", FLOAT_PROJECT_BBQS, PERSON_MARIO);
    private static final Task TASK_MARIO_HOLIDAYS = givenATask("Holidays", "Holiday / Sick Leave", PERSON_MARIO);

    private static final Task[] ALL_TASKS = new Task[]{
            TASK_MARIO_ALL_4,
            TASK_LUIGI_X,
            TASK_PEACH_X,
            TASK_MARIO_BBQ,
            TASK_MARIO_HOLIDAYS
    };

    @Mock
    private GithubUserConverter mockFloatGithubUserConverter;

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
        return new Task(taskName, projectName, person.getName(), Integer.toString(person.getId()));
    }

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        given(mockPeopleServiceClient)
                .hasPeople(PERSON_MARIO, PERSON_LUIGI, PERSON_PEACH);

        given(mockFloatGithubUserConverter)
                .hasMapping(GITHUB_MARIO, PERSON_MARIO.getName())
                .hasMapping(GITHUB_LUIGI, PERSON_LUIGI.getName())
                .hasMapping(GITHUB_PEACH, PERSON_PEACH.getName())
                .hasGithubUsers(GITHUB_MARIO, GITHUB_LUIGI, GITHUB_PEACH);

        BDDMockito.given(mockFloatGithubProjectConverter.getRepositories(FLOAT_PROJECT_ALL_4))
                .willReturn(asList(GITHUB_ALL_4, FLOAT_PROJECT_NOVODA_TV));

        BDDMockito.given(mockNumberOfWeeksCalculator.getNumberOfWeeksOrNullIn(ANY_START_DATE, ANY_END_DATE))
                .willReturn(ANY_NUMBER_OF_WEEKS);

        BDDMockito.given(floatDateConverter.fromFloatDateFormatOrNoDate(ANY_FLOAT_START_DATE)).willReturn(ANY_START_DATE);
        BDDMockito.given(floatDateConverter.fromFloatDateFormatOrNoDate(ANY_FLOAT_END_DATE)).willReturn(ANY_END_DATE);
        BDDMockito.given(floatDateConverter.toFloatDateFormat(ANY_START_DATE, ANY_TIMEZONE)).willReturn(ANY_FLOAT_START_DATE);
        BDDMockito.given(floatDateConverter.toFloatDateFormat(ANY_END_DATE, ANY_TIMEZONE)).willReturn(ANY_FLOAT_END_DATE);
    }

    private GivenTaskServiceClient given(TaskServiceClient taskServiceClient) {
        return new GivenTaskServiceClient(taskServiceClient);
    }

    private GivenPeopleServiceClient given(PeopleServiceClient peopleServiceClient) {
        return new GivenPeopleServiceClient(peopleServiceClient);
    }

    private GivenFloatGithubUserConverter given(GithubUserConverter floatGithubUserConverter) {
        return new GivenFloatGithubUserConverter(floatGithubUserConverter);
    }

    @Test
    public void givenAllTasks_whenGettingRepositoryNamesForFloatUser_thenTheExpectedRepositoryNamesAreEmitted() {
        given(mockTaskServiceClient).hasAllTasks();

        Observable<String> actual = floatServiceClient
                .getRepositoryNamesForFloatUser(FLOAT_MARIO, ANY_START_DATE, ANY_NUMBER_OF_WEEKS, ANY_TIMEZONE);

        assertThatAnObservable(actual).hasEmittedValues(GITHUB_ALL_4, FLOAT_PROJECT_NOVODA_TV);
    }

    @Test
    public void givenAllTasks_whenGettingRepositoryNamesForGithubUser_thenTheExpectedRepositoryNamesAreEmitted()
            throws FailedToLoadMappingsException, NoMatchFoundException {

        given(mockTaskServiceClient).hasAllTasks();

        Observable<String> actual = floatServiceClient
                .getRepositoryNamesForGithubUser(GITHUB_MARIO, ANY_START_DATE, ANY_NUMBER_OF_WEEKS, ANY_TIMEZONE);

        assertThatAnObservable(actual).hasEmittedValues(GITHUB_ALL_4, FLOAT_PROJECT_NOVODA_TV);
    }

    @Test
    public void givenNoTasks_whenGettingRepositoryNamesForGithubUserWithNoTasksAssigned_thenNoItemsAreEmitted() {
        given(mockTaskServiceClient).hasNoTasks();

        Observable<String> actual = floatServiceClient
                .getRepositoryNamesForFloatUser(FLOAT_MARIO, ANY_START_DATE, ANY_NUMBER_OF_WEEKS, ANY_TIMEZONE);

        assertThatAnObservable(actual).hasEmittedNoValues();
    }

    @Test
    public void givenTasksForPerson_whenGettingTasksForFloatRelatedUser_thenWeOnlyGetTasksForThatUser() throws Exception {
        given(mockTaskServiceClient).hasTasks(TASK_MARIO_ALL_4, TASK_MARIO_BBQ);

        Observable<Task> actual = floatServiceClient
                .getTasksForFloatUser(FLOAT_MARIO, ANY_START_DATE, ANY_NUMBER_OF_WEEKS, ANY_TIMEZONE);

        assertThatAnObservable(actual).hasEmittedValues(
                assertTaskNameIsEqual(),
                TASK_MARIO_ALL_4, TASK_MARIO_BBQ
        );
    }

    @Test
    public void givenAllTasks_whenGettingTasksForFloatUser_thenWeOnlyGetTasksThatAreNotHolidays()
            throws Exception {

        given(mockTaskServiceClient).hasAllTasks();

        Observable<Task> actual = floatServiceClient
                .getTasksForFloatUser(FLOAT_MARIO, ANY_START_DATE, ANY_NUMBER_OF_WEEKS, ANY_TIMEZONE);

        assertThatAnObservable(actual)
                .hasEmittedValues(assertTaskNameDoesNotContainHoliday());

    }

    private Action1<Task> assertTaskNameDoesNotContainHoliday() {
        return task -> assertEquals(false, task.getName().contains("Holiday"));
    }

    @Test
    public void givenTasksForPerson_whenGettingTasksForRelatedGithubUser_thenWeOnlyGetTasksForCorrespondingFloatUser()
            throws Exception {

        given(mockTaskServiceClient)
                .hasTasksForOnePerson(PERSON_MARIO, TASK_MARIO_ALL_4, TASK_MARIO_BBQ);

        Observable<Task> actual = floatServiceClient
                .getTasksForGithubUser(GITHUB_MARIO, ANY_START_DATE, ANY_NUMBER_OF_WEEKS, ANY_TIMEZONE);

        assertThatAnObservable(actual).hasEmittedValues(assertTaskNameIsEqual(), TASK_MARIO_ALL_4, TASK_MARIO_BBQ);
    }

    private TestSubscriberAssert.TestSubscriberCustomAssert<Task> assertTaskNameIsEqual() {
        return (expectedTask, actualTask) -> assertEquals(expectedTask.getName(), actualTask.getName());
    }

    @Test
    public void givenAllTasks_whenGettingTasksForMultipleGithubUsers_thenWeGetTasksMappedToGithubUsers() {
        given(mockTaskServiceClient).hasAllTasks();

        List<String> someGithubUsers = asList(GITHUB_MARIO, GITHUB_PEACH);

        Observable<Map.Entry<String, List<Task>>> actual = floatServiceClient
                .getTasksForGithubUsers(someGithubUsers, ANY_START_DATE, ANY_END_DATE, ANY_TIMEZONE);

        assertThatAnObservable(actual)
                .hasEmittedValues(
                        aMapEntry(GITHUB_MARIO, asList(TASK_MARIO_ALL_4, TASK_MARIO_BBQ)),
                        aMapEntry(GITHUB_PEACH, asList(TASK_PEACH_X))
                );
    }

    private Map.Entry aMapEntry(String githubMario, List<Task> tasks) {
        return new AbstractMap.SimpleImmutableEntry<>(githubMario, tasks);
    }

    @Test
    public void givenFailingFloatUsernameLookup_whenGettingTasksForGithubUser_thenWeGetErroringObservable()
            throws FailedToLoadMappingsException {

        given(mockFloatGithubUserConverter).failsLookupForGithubUsername(GITHUB_MARIO);

        Observable<Task> actual = floatServiceClient
                .getTasksForGithubUser(GITHUB_MARIO, ANY_START_DATE, ANY_NUMBER_OF_WEEKS, ANY_TIMEZONE);

        assertThatAnObservable(actual).hasThrown(FailedToLoadMappingsException.class);
    }

    @Test
    public void givenListOfGithubUsernames_whenConvertToFloatUsernames_thenReturnMatchingFloatUsernames() {
        List<String> githubUsernames = asList(GITHUB_MARIO, GITHUB_PEACH);

        Map<String, String> expected = new HashMap<>();
        expected.put(FLOAT_MARIO, GITHUB_MARIO);
        expected.put(FLOAT_PEACH, GITHUB_PEACH);

        Map<String, String> actual = floatServiceClient.mapFloatToGithubUsernames(githubUsernames);

        assertEquals(expected, actual);
    }

    @Test
    public void givenListOfGithubUsernamesWithUsernameNotMatchedInFloat_whenConvertToFloatUsernames_thenThrowGithubToFloatUserMatchNotFoundException()
            throws FailedToLoadMappingsException {

        // given
        String nonExistingGithubUsername = "this-user-does-not-exist-on-float";
        given(mockFloatGithubUserConverter).failsLookupForGithubUsername(nonExistingGithubUsername);

        // expect exception
        expectedException.expect(GithubToFloatUserMatchNotFoundException.class);

        // when
        List<String> githubUsernames = asList(GITHUB_MARIO, GITHUB_PEACH, nonExistingGithubUsername);
        floatServiceClient.mapFloatToGithubUsernames(githubUsernames);
    }

    @Test
    public void givenListOfGithubUsernamesAndAllTasks_whenGetGithubUsersAssignmentsInDateRange_thenReturnMapOfUserAssignments() {
        given(mockTaskServiceClient).hasAllTasks();

        Map<String, List<UserAssignments>> expected = new HashMap<>();
        expected.put(GITHUB_MARIO, asList(
                createUserAssignments(FLOAT_PROJECT_ALL_4, GITHUB_ALL_4, FLOAT_PROJECT_NOVODA_TV),
                createUserAssignments(FLOAT_PROJECT_BBQS)
        ));
        expected.put(GITHUB_PEACH, asList(
                createUserAssignments(FLOAT_PROJECT_X)
        ));

        Map<String, List<UserAssignments>> actual = floatServiceClient.getGithubUsersAssignmentsInDateRange(
                asList(GITHUB_MARIO, GITHUB_PEACH),
                ANY_START_DATE,
                ANY_END_DATE,
                ANY_TIMEZONE
        );

        assertEquals(expected, actual);
    }

    private UserAssignments createUserAssignments(String projectName, String... repositories) {
        return UserAssignments.builder()
                .assignedProject(projectName)
                .assignedRepositories(asList(repositories))
                .build();
    }

    @Test
    public void givenEmptyListOfGithubUsernamesAndAllTasks_whenGetGithubUsersAssignmentsInDateRange_thenReturnMapOfAllGithubUsers() {
        List<String> emptyGithubUsernames = emptyList();
        given(mockTaskServiceClient).hasAllTasks();

        Map<String, List<UserAssignments>> actualMap = floatServiceClient.getGithubUsersAssignmentsInDateRange(
                emptyGithubUsernames,
                ANY_START_DATE,
                ANY_END_DATE,
                ANY_TIMEZONE
        );

        assertAllPersonsInKeySet(actualMap.keySet());
    }

    @Test
    public void givenNullListOfGithubUsernamesAndAllTasks_whenGetGithubUsersAssignmentsInDateRange_thenReturnMapOfAllGithubUsers() {
        List<String> nullGithubUsernames = null;
        given(mockTaskServiceClient).hasAllTasks();

        Map<String, List<UserAssignments>> actualMap = floatServiceClient.getGithubUsersAssignmentsInDateRange(
                nullGithubUsernames,
                ANY_START_DATE,
                ANY_END_DATE,
                ANY_TIMEZONE
        );

        assertAllPersonsInKeySet(actualMap.keySet());
    }

    private void assertAllPersonsInKeySet(Set<String> actual) {
        assertEquals(new HashSet<>(asList(
                GITHUB_MARIO,
                GITHUB_LUIGI,
                GITHUB_PEACH
        )), actual);
    }

    private class GivenTaskServiceClient {
        private final TaskServiceClient taskServiceClient;

        GivenTaskServiceClient(TaskServiceClient taskServiceClient) {
            this.taskServiceClient = taskServiceClient;
        }

        GivenTaskServiceClient hasTasksForOnePerson(Person person, Task... tasks) {
            return hasTasksForOnePerson(person.getId(), tasks);
        }

        GivenTaskServiceClient hasTasksForOnePerson(int personId, Task... tasks) {
            Observable<Task> mockTasksObservable = Observable.from(Arrays.asList(tasks));

            BDDMockito.given(taskServiceClient.getTasks(
                    any(Date.class),
                    anyInt(),
                    any(TimeZone.class),
                    eq(personId))
            ).willReturn(mockTasksObservable);

            return this;
        }

        GivenTaskServiceClient hasAllTasks() {
            return hasTasks(ALL_TASKS);
        }

        private GivenTaskServiceClient hasNoTasks() {
            return hasTasks();
        }

        private GivenTaskServiceClient hasTasks(Task... tasks) {
            Observable<Task> mockTasksObservable = Observable.from(Arrays.asList(tasks));

            BDDMockito.given(
                    taskServiceClient.getTasksForAllPeople(
                            any(Date.class),
                            anyInt(),
                            any(TimeZone.class)
                    )
            ).willReturn(mockTasksObservable);

            Observable.from(tasks)
                    .groupBy(Task::getPersonId)
                    .flatMap(Observable::toList)
                    .subscribe(userTasks -> this.hasTasksForOnePerson(
                            Integer.parseInt(userTasks.get(0).getPersonId()),
                            userTasks.toArray(new Task[]{}))
                    );

            return this;
        }
    }

    private class GivenFloatGithubUserConverter {
        private final GithubUserConverter floatGithubUserConverter;

        GivenFloatGithubUserConverter(GithubUserConverter floatGithubUserConverter) {
            this.floatGithubUserConverter = floatGithubUserConverter;
        }

        GivenFloatGithubUserConverter hasMapping(String githubUsername, String floatUsername) throws FailedToLoadMappingsException {
            BDDMockito.given(floatGithubUserConverter.getGithubUser(floatUsername)).willReturn(githubUsername);
            BDDMockito.given(floatGithubUserConverter.getFloatUser(githubUsername)).willReturn(floatUsername);
            return this;
        }

        GivenFloatGithubUserConverter hasGithubUsers(String... githubUsernames) throws FailedToLoadMappingsException {
            BDDMockito.given(floatGithubUserConverter.getGithubUsers()).willReturn(asList(githubUsernames));
            return this;
        }

        GivenFloatGithubUserConverter failsLookupForGithubUsername(String githubUsername) throws FailedToLoadMappingsException {
            BDDMockito.given(floatGithubUserConverter.getFloatUser(githubUsername)).willThrow(FailedToLoadMappingsException.class);
            return this;
        }
    }

    private class GivenPeopleServiceClient {
        private final PeopleServiceClient peopleServiceClient;

        GivenPeopleServiceClient(PeopleServiceClient peopleServiceClient) {
            this.peopleServiceClient = peopleServiceClient;
        }

        GivenPeopleServiceClient hasPeople(Person... people) {
            Observable<Person> mockPersonsObservable = Observable.from(people);
            BDDMockito.given(peopleServiceClient.getPersons()).willReturn(mockPersonsObservable);
            return this;
        }
    }
}
