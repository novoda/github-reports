package com.novoda.floatschedule;

import com.novoda.floatschedule.convert.*;
import com.novoda.floatschedule.people.PeopleServiceClient;
import com.novoda.floatschedule.people.Person;
import com.novoda.floatschedule.task.Task;
import com.novoda.floatschedule.task.TaskServiceClient;
import com.novoda.github.reports.data.model.UserAssignments;
import rx.Observable;
import rx.functions.Action2;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.internal.util.UtilityFunctions;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FloatServiceClient {

    private final FloatGithubUserConverter floatGithubUserConverter;
    private final FloatGithubProjectConverter floatGithubProjectConverter;
    private final PeopleServiceClient peopleServiceClient;
    private final TaskServiceClient taskServiceClient;
    private final NumberOfWeeksCalculator numberOfWeeksCalculator;
    private final FloatDateConverter floatDateConverter;

    public static FloatServiceClient newInstance() {
        FloatGithubUserConverter floatGithubUserConverter = FloatGithubUserConverter.newInstance();
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

    private FloatServiceClient(FloatGithubUserConverter floatGithubUserConverter,
                               FloatGithubProjectConverter floatGithubProjectConverter,
                               PeopleServiceClient peopleServiceClient,
                               TaskServiceClient taskServiceClient,
                               NumberOfWeeksCalculator numberOfWeeksCalculator, FloatDateConverter floatDateConverter) {

        this.floatGithubUserConverter = floatGithubUserConverter;
        this.floatGithubProjectConverter = floatGithubProjectConverter;
        this.peopleServiceClient = peopleServiceClient;
        this.taskServiceClient = taskServiceClient;
        this.numberOfWeeksCalculator = numberOfWeeksCalculator;
        this.floatDateConverter = floatDateConverter;
    }

    Observable<String> getRepositoryNamesForGithubUser(String githubUsername, Date startDate, int numberOfWeeks)
            throws IOException, NoMatchFoundException {

        return getRepositoryNamesForFloatUser(getFloatUsername(githubUsername), startDate, numberOfWeeks);
    }

    Observable<String> getRepositoryNamesForFloatUser(String floatUsername, Date startDate, int numberOfWeeks) {
        return getTasksForFloatUser(floatUsername, startDate, numberOfWeeks)
                .map(this::getRepositoriesFor)
                .collect((Func0<List<String>>) ArrayList::new, List::addAll)
                .flatMapIterable(UtilityFunctions.identity())
                .distinct();
    }

    Observable<Task> getTasksForGithubUser(String githubUsername, Date startDate, Integer numberOfWeeks) {
        String floatUsername;
        try {
            floatUsername = getFloatUsername(githubUsername);
        } catch (IOException e) {
            return Observable.error(e);
        }
        return getTasksForFloatUser(floatUsername, startDate, numberOfWeeks);
    }

    private String getFloatUsername(String githubUsername) throws IOException, NoMatchFoundException {
        return floatGithubUserConverter.getFloatUser(githubUsername);
    }

    Observable<Task> getTasksForFloatUser(String floatUsername, Date startDate, Integer numberOfWeeks) {
        return peopleServiceClient.getPersons()
                .filter(byFloatUsername(floatUsername))
                .flatMap(toTasks(startDate, numberOfWeeks))
                .filter(excludingHolidays());
    }

    private Func1<Person, Boolean> byFloatUsername(String floatUsername) {
        return person -> personHasFloatUsername(person, floatUsername);
    }

    private Func1<Person, Observable<Task>> toTasks(Date startDate, Integer numberOfWeeks) {
        return person -> taskServiceClient.getTasks(startDate, numberOfWeeks, person.getId());
    }

    public List<String> getRepositoriesFor(Task task) {
        try {
            return floatGithubProjectConverter.getRepositories(task.getProjectName());
        } catch (IOException | NoMatchFoundException e) {
            // ignored
        }
        return Collections.emptyList();
    }

    // TODO: test this
    public HashMap<String, List<UserAssignments>> getGithubUsersAssignmentsInDateRange(List<String> githubUsers,
                                                                                       Date from,
                                                                                       Date to) {

        if (listIsNullOrEmpty(githubUsers)) {
            githubUsers = getGithubUsersOrEmpty();
        }

        return getTasksForGithubUsers(githubUsers, from, to)
                .map(tasksToUserAssignments())
                .collect(
                        HashMap<String, List<UserAssignments>>::new,
                        putEntryInMap()
                )
                .toBlocking()
                .first();
    }

    private boolean listIsNullOrEmpty(List<String> githubUsers) {
        return githubUsers == null || githubUsers.isEmpty();
    }

    private List<String> getGithubUsersOrEmpty() {
        try {
            return floatGithubUserConverter.getGithubUsers();
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    public Observable<Map.Entry<String, List<Task>>> getTasksForGithubUsers(List<String> githubUsernames,
                                                                            Date startDate,
                                                                            Date endDate) {

        Integer numberOfWeeks = numberOfWeeksCalculator.getNumberOfWeeksOrNullIn(startDate, endDate);
        return getTasksForGithubUsers(githubUsernames, startDate, numberOfWeeks);
    }

    private Observable<Map.Entry<String, List<Task>>> getTasksForGithubUsers(List<String> githubUsernames,
                                                                             Date startDate,
                                                                             Integer numberOfWeeks) {

        Map<String, String> floatToGithubUsernames;
        try {
            floatToGithubUsernames = mapFloatToGithubUsernames(githubUsernames);
        } catch (GithubToFloatUserMatchNotFoundException floatUserNotFound) {
            return Observable.error(floatUserNotFound);
        }

        return peopleServiceClient.getPersons()
                .filter(byFloatUsernames(floatToGithubUsernames.keySet()))
                .map(personToGithubUserWithTasksEntry(startDate, numberOfWeeks, floatToGithubUsernames));
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

    Map<String, String> mapFloatToGithubUsernames(List<String> githubUsernames)
            throws GithubToFloatUserMatchNotFoundException {

        return githubUsernames
                .stream()
                .map(githubToFloatUsername())
                .collect(
                        HashMap::new,
                        (map, entry) -> map.put(entry.getKey(), entry.getValue()),
                        (combineThis, withThis) -> {}
                );
    }

    private Function<String, AbstractMap.SimpleImmutableEntry<String, String>> githubToFloatUsername() {
        return githubUsername -> {
            try {
                String floatUsername = floatGithubUserConverter.getFloatUser(githubUsername);
                return new AbstractMap.SimpleImmutableEntry<>(floatUsername, githubUsername);
            } catch (IOException e) {
                throw new GithubToFloatUserMatchNotFoundException(e);
            }
        };
    }

    private Func1<Person, Map.Entry<String, List<Task>>> personToGithubUserWithTasksEntry(Date startDate,
                                                                                          Integer numberOfWeeks,
                                                                                          Map<String, String> floatToGithubUsernames) {

        return person -> {
            List<Task> personTasks = taskServiceClient
                    .getTasks(startDate, numberOfWeeks, person.getId())
                    .filter(excludingHolidays())
                    .toList()
                    .toBlocking()
                    .first();
            String floatUsername = person.getName();
            String githubUsername = floatToGithubUsernames.get(floatUsername);

            return new AbstractMap.SimpleImmutableEntry<>(
                    githubUsername,
                    personTasks
            );
        };
    }

    private Func1<Task, Boolean> excludingHolidays() {
        return task -> task.getName() != null &&
                !(task.getName().contains("holiday") || task.getName().contains("Holiday"));
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
