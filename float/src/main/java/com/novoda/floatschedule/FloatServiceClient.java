package com.novoda.floatschedule;

import com.novoda.floatschedule.convert.FailedToLoadMappingsException;
import com.novoda.floatschedule.convert.FloatDateConverter;
import com.novoda.floatschedule.convert.FloatGithubProjectConverter;
import com.novoda.floatschedule.convert.GithubProjectConverter;
import com.novoda.floatschedule.convert.GithubToFloatUserMatchNotFoundException;
import com.novoda.floatschedule.convert.GithubUserConverter;
import com.novoda.floatschedule.convert.NoMatchFoundException;
import com.novoda.floatschedule.convert.NumberOfWeeksCalculator;
import com.novoda.floatschedule.convert.SheetsFloatGithubUserConverter;
import com.novoda.floatschedule.people.PeopleServiceClient;
import com.novoda.floatschedule.people.Person;
import com.novoda.floatschedule.task.Task;
import com.novoda.floatschedule.task.TaskServiceClient;
import com.novoda.github.reports.data.model.UserAssignments;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import rx.Observable;
import rx.functions.Action2;
import rx.functions.Func1;

public class FloatServiceClient {

    private static final String HOLIDAY_TASK_DESCRIPTOR = "HOLIDAY";

    private final GithubUserConverter floatGithubUserConverter;
    private final GithubProjectConverter floatGithubProjectConverter;
    private final PeopleServiceClient peopleServiceClient;
    private final TaskServiceClient taskServiceClient;
    private final NumberOfWeeksCalculator numberOfWeeksCalculator;
    private final FloatDateConverter floatDateConverter;

    public static FloatServiceClient newInstance() {
        GithubUserConverter floatGithubUserConverter = SheetsFloatGithubUserConverter.newInstance();
        FloatGithubProjectConverter floatGithubProjectConverter = FloatGithubProjectConverter.newInstance();
        PeopleServiceClient peopleServiceClient = PeopleServiceClient.newInstance();
        TaskServiceClient taskServiceClient = TaskServiceClient.newInstance();
        NumberOfWeeksCalculator numberOfWeeksCalculator = new NumberOfWeeksCalculator();
        FloatDateConverter floatDateConverter = new FloatDateConverter();

        return new FloatServiceClient(
                floatGithubUserConverter,
                floatGithubProjectConverter,
                peopleServiceClient,
                taskServiceClient,
                numberOfWeeksCalculator,
                floatDateConverter);
    }

    private FloatServiceClient(GithubUserConverter floatGithubUserConverter,
                               FloatGithubProjectConverter floatGithubProjectConverter,
                               PeopleServiceClient peopleServiceClient,
                               TaskServiceClient taskServiceClient,
                               NumberOfWeeksCalculator numberOfWeeksCalculator,
                               FloatDateConverter floatDateConverter) {

        this.floatGithubUserConverter = floatGithubUserConverter;
        this.floatGithubProjectConverter = floatGithubProjectConverter;
        this.peopleServiceClient = peopleServiceClient;
        this.taskServiceClient = taskServiceClient;
        this.numberOfWeeksCalculator = numberOfWeeksCalculator;
        this.floatDateConverter = floatDateConverter;
    }

    public HashMap<String, List<UserAssignments>> getGithubUsersAssignmentsInDateRange(List<String> githubUsers,
                                                                                       Date from,
                                                                                       Date to,
                                                                                       TimeZone timezone) {

        if (listIsNullOrEmpty(githubUsers)) {
            githubUsers = getGithubUsersOrEmpty();
        }

        return getTasksForGithubUsers(githubUsers, from, to, timezone)
                .map(tasksToUserAssignments())
                .collect(HashMap<String, List<UserAssignments>>::new, putEntryInMap())
                .toBlocking()
                .first();
    }

    private boolean listIsNullOrEmpty(List<String> githubUsers) {
        return githubUsers == null || githubUsers.isEmpty();
    }

    private List<String> getGithubUsersOrEmpty() {
        try {
            return floatGithubUserConverter.getGithubUsers();
        } catch (FailedToLoadMappingsException e) {
            return Collections.emptyList();
        }
    }

    Observable<Map.Entry<String, List<Task>>> getTasksForGithubUsers(List<String> githubUsernames,
                                                                     Date startDate,
                                                                     Date endDate,
                                                                     TimeZone timezone) {

        Integer numberOfWeeks = numberOfWeeksCalculator.getNumberOfWeeksOrNullIn(startDate, endDate);
        return getTasksForGithubUsers(githubUsernames, startDate, numberOfWeeks, timezone);
    }

    private Observable<Map.Entry<String, List<Task>>> getTasksForGithubUsers(List<String> githubUsernames,
                                                                             Date startDate,
                                                                             Integer numberOfWeeks,
                                                                             TimeZone timezone) {

        Map<String, String> floatToGithubUsernames;
        try {
            floatToGithubUsernames = mapFloatToGithubUsernames(githubUsernames);
        } catch (GithubToFloatUserMatchNotFoundException floatUserNotFound) {
            return Observable.error(floatUserNotFound);
        }

        return peopleServiceClient.getPersons()
                .filter(byFloatUsernames(floatToGithubUsernames.keySet()))
                .toList()
                .flatMap(peopleToGithubUserWithTasksEntry(startDate, numberOfWeeks, timezone, floatToGithubUsernames));
    }

    Map<String, String> mapFloatToGithubUsernames(List<String> githubUsernames)
            throws GithubToFloatUserMatchNotFoundException {

        return githubUsernames
                .stream()
                .map(githubToFloatUsername())
                .collect(
                        HashMap::new,
                        (map, entry) -> map.put(entry.getKey(), entry.getValue()),
                        (combineThis, withThis) -> {
                        }
                );
    }

    private Function<String, AbstractMap.SimpleImmutableEntry<String, String>> githubToFloatUsername() {
        return githubUsername -> {
            try {
                String floatUsername = floatGithubUserConverter.getFloatUser(githubUsername);
                return new AbstractMap.SimpleImmutableEntry<>(floatUsername, githubUsername);
            } catch (FailedToLoadMappingsException e) {
                throw new GithubToFloatUserMatchNotFoundException(e);
            }
        };
    }

    private Func1<Person, Boolean> byFloatUsernames(Collection<String> floatUsernames) {
        return person -> floatUsernames
                .stream()
                .filter(byPersonName(person))
                .count() > 0;
    }

    private Predicate<String> byPersonName(Person person) {
        return floatUsername -> personHasFloatUsername(person, floatUsername);
    }

    private boolean personHasFloatUsername(Person person, String floatUsername) {
        return person.getName().equalsIgnoreCase(floatUsername);
    }

    private Func1<List<Person>, Observable<? extends Map.Entry<String, List<Task>>>> peopleToGithubUserWithTasksEntry(Date startDate,
                                                                                                                      Integer numberOfWeeks,
                                                                                                                      TimeZone timezone,
                                                                                                                      Map<String, String> floatToGithubUsernames) {

        return persons -> taskServiceClient
                .getTasksForAllPeople(startDate, numberOfWeeks, timezone)
                .filter(excludingHolidays())
                .filter(excludingTaskForNonRequestedPeople(persons))
                .groupBy(Task::getPersonId)
                .flatMap(Observable::toList)
                .map(personTasksToGithubUserWithTasksEntry(floatToGithubUsernames));
    }

    private Func1<Task, Boolean> excludingTaskForNonRequestedPeople(List<Person> persons) {
        return task -> persons
                .stream()
                .anyMatch(person -> Integer.toString(person.getId()).equals(task.getPersonId()));
    }

    private Func1<Task, Boolean> excludingHolidays() {
        return task -> task.getName() == null ||
                !(task.getName().toUpperCase().contains(HOLIDAY_TASK_DESCRIPTOR));
    }

    private Func1<List<Task>, AbstractMap.SimpleImmutableEntry<String, List<Task>>> personTasksToGithubUserWithTasksEntry(Map<String, String> floatToGithubUsernames) {
        return tasks -> {
            String floatUsername = tasks.get(0).getPersonName();
            String githubUsername = floatToGithubUsernames.get(floatUsername);

            return new AbstractMap.SimpleImmutableEntry<>(
                    githubUsername,
                    tasks
            );
        };
    }

    private Function<Task, UserAssignments> taskToUserAssignments() {
        return task -> {
            Date assignmentStart = floatDateConverter.fromFloatDateFormatOrNoDate(task.getStartDate());
            Date assignmentEnd = floatDateConverter.fromFloatDateFormatOrNoDate(task.getEndDate());
            List<String> repositoriesForTask = getRepositoriesFor(task);

            return UserAssignments.builder()
                    .assignmentStart(assignmentStart)
                    .assignmentEnd(assignmentEnd)
                    .assignedProject(task.getProjectName())
                    .assignedRepositories(repositoriesForTask)
                    .build();
        };
    }

    private List<String> getRepositoriesFor(Task task) {
        try {
            return floatGithubProjectConverter.getRepositories(task.getProjectName());
        } catch (FailedToLoadMappingsException | NoMatchFoundException e) {
            // ignored
        }
        return Collections.emptyList();
    }

    private Func1<Map.Entry<String, List<Task>>, AbstractMap.SimpleImmutableEntry<String, List<UserAssignments>>> tasksToUserAssignments() {
        return usernameWithTasks -> {
            String username = usernameWithTasks.getKey();
            List<UserAssignments> assignments = usernameWithTasks.getValue()
                    .stream()
                    .map(taskToUserAssignments())
                    .collect(Collectors.toList());
            return new AbstractMap.SimpleImmutableEntry<>(username, assignments);
        };
    }

    private Action2<HashMap<String, List<UserAssignments>>, AbstractMap.SimpleImmutableEntry<String, List<UserAssignments>>> putEntryInMap() {
        return (map, entry) -> map.put(entry.getKey(), entry.getValue());
    }
}
