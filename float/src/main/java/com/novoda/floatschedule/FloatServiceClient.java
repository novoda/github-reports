package com.novoda.floatschedule;

import com.novoda.floatschedule.convert.FloatGithubProjectConverter;
import com.novoda.floatschedule.convert.FloatGithubUserConverter;
import com.novoda.floatschedule.convert.NoMatchFoundException;
import com.novoda.floatschedule.convert.NumberOfWeeksCalculator;
import com.novoda.floatschedule.people.PeopleServiceClient;
import com.novoda.floatschedule.people.Person;
import com.novoda.floatschedule.task.Task;
import com.novoda.floatschedule.task.TaskServiceClient;
import rx.Observable;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.internal.util.UtilityFunctions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class FloatServiceClient {

    private final FloatGithubUserConverter floatGithubUserConverter;
    private final FloatGithubProjectConverter floatGithubProjectConverter;
    private final PeopleServiceClient peopleServiceClient;
    private final TaskServiceClient taskServiceClient;
    private final NumberOfWeeksCalculator numberOfWeeksCalculator;

    public static FloatServiceClient newInstance() {
        FloatGithubUserConverter floatGithubUserConverter = FloatGithubUserConverter.newInstance();
        FloatGithubProjectConverter floatGithubProjectConverter = FloatGithubProjectConverter.newInstance();
        PeopleServiceClient peopleServiceClient = PeopleServiceClient.newInstance();
        TaskServiceClient taskServiceClient = TaskServiceClient.newInstance();
        NumberOfWeeksCalculator numberOfWeeksCalculator = new NumberOfWeeksCalculator();

        return new FloatServiceClient(
                floatGithubUserConverter,
                floatGithubProjectConverter,
                peopleServiceClient,
                taskServiceClient,
                numberOfWeeksCalculator
        );
    }

    private FloatServiceClient(FloatGithubUserConverter floatGithubUserConverter,
                               FloatGithubProjectConverter floatGithubProjectConverter,
                               PeopleServiceClient peopleServiceClient,
                               TaskServiceClient taskServiceClient,
                               NumberOfWeeksCalculator numberOfWeeksCalculator) {

        this.floatGithubUserConverter = floatGithubUserConverter;
        this.floatGithubProjectConverter = floatGithubProjectConverter;
        this.peopleServiceClient = peopleServiceClient;
        this.taskServiceClient = taskServiceClient;
        this.numberOfWeeksCalculator = numberOfWeeksCalculator;
    }

    Observable<String> getRepositoryNamesForGithubUser(String githubUsername, Date startDate, int numberOfWeeks)
            throws IOException, NoMatchFoundException {

        return getRepositoryNamesForFloatUser(getFloatUsername(githubUsername), startDate, numberOfWeeks);
    }

    Observable<String> getRepositoryNamesForFloatUser(String floatUsername, Date startDate, int numberOfWeeks) {
        return getTasksForGithubUser(floatUsername, startDate, numberOfWeeks)
                .map(this::getRepositoriesFor)
                .collect((Func0<List<String>>) ArrayList::new, List::addAll)
                .flatMapIterable(UtilityFunctions.identity())
                .distinct();
    }

    public List<String> getRepositoriesFor(Task task) {
        try {
            return floatGithubProjectConverter.getRepositories(task.getProjectName());
        } catch (IOException | NoMatchFoundException e) {
            // ignored
        }
        return Collections.emptyList();
    }

    public Observable<Task> getTasksForGithubUser(String githubUsername, Date startDate, Date endDate) {
        int numberOfWeeks = numberOfWeeksCalculator.getNumberOfWeeksIn(startDate, endDate);
        return getTasksForGithubUsername(githubUsername, startDate, numberOfWeeks);
    }

    private Observable<Task> getTasksForGithubUsername(String githubUsername, Date startDate, int numberOfWeeks) {
        try {
            return getTasksForGithubUser(getFloatUsername(githubUsername), startDate, numberOfWeeks);
        } catch (IOException e) {
            return Observable.error(e);
        }
    }

    private String getFloatUsername(String githubUsername) throws IOException, NoMatchFoundException {
        return floatGithubUserConverter.getFloatUser(githubUsername);
    }

    Observable<Task> getTasksForGithubUser(String floatUsername, Date startDate, int numberOfWeeks) {
        return peopleServiceClient.getPersons()
                .filter(byFloatUsername(floatUsername))
                .flatMap(toTasks(startDate, numberOfWeeks))
                .filter(excludingHolidays());
    }

    private Func1<Person, Boolean> byFloatUsername(String floatUsername) {
        return person -> person.getName().equalsIgnoreCase(floatUsername);
    }

    private Func1<Person, Observable<? extends Task>> toTasks(Date startDate, int numberOfWeeks) {
        return person -> taskServiceClient.getTasks(startDate, numberOfWeeks, person.getId());
    }

    private Func1<Task, Boolean> excludingHolidays() {
        return task -> task.getName() != null && !(task.getName().contains("holiday") || task.getName().contains("Holiday"));
    }
}
