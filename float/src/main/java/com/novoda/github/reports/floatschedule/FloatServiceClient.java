package com.novoda.github.reports.floatschedule;

import com.novoda.github.reports.floatschedule.convert.FloatGithubProjectConverter;
import com.novoda.github.reports.floatschedule.convert.FloatGithubUserConverter;
import com.novoda.github.reports.floatschedule.convert.NoMatchFoundException;
import com.novoda.github.reports.floatschedule.people.PeopleServiceClient;
import com.novoda.github.reports.floatschedule.task.Task;
import com.novoda.github.reports.floatschedule.task.TaskServiceClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.functions.Func0;
import rx.internal.util.UtilityFunctions;

public class FloatServiceClient {

    private final FloatGithubUserConverter floatGithubUserConverter;
    private final FloatGithubProjectConverter floatGithubProjectConverter;
    private final PeopleServiceClient peopleServiceClient;
    private final TaskServiceClient taskServiceClient;

    public static FloatServiceClient newInstance() {
        FloatGithubUserConverter floatGithubUserConverter = FloatGithubUserConverter.newInstance();
        FloatGithubProjectConverter floatGithubProjectConverter = FloatGithubProjectConverter.newInstance();
        PeopleServiceClient peopleServiceClient = PeopleServiceClient.newInstance();
        TaskServiceClient taskServiceClient = TaskServiceClient.newInstance();
        return new FloatServiceClient(floatGithubUserConverter, floatGithubProjectConverter, peopleServiceClient, taskServiceClient);
    }

    FloatServiceClient(FloatGithubUserConverter floatGithubUserConverter,
                       FloatGithubProjectConverter floatGithubProjectConverter,
                       PeopleServiceClient peopleServiceClient,
                       TaskServiceClient taskServiceClient) {

        this.floatGithubUserConverter = floatGithubUserConverter;
        this.floatGithubProjectConverter = floatGithubProjectConverter;
        this.peopleServiceClient = peopleServiceClient;
        this.taskServiceClient = taskServiceClient;
    }

    Observable<String> getGithubRepositories(String githubUsername) {
        // TODO start date, end date

        return getTasksFor(getFloatUsername(githubUsername))
                .map(this::getRepositoriesFor)
                .collect((Func0<List<String>>) ArrayList::new, List::addAll)
                .flatMapIterable(UtilityFunctions.identity())
                .distinct();
    }

    private String getFloatUsername(String githubUsername) {
        try {
            return floatGithubUserConverter.getFloatUser(githubUsername);
        } catch (IOException | NoMatchFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    private List<String> getRepositoriesFor(Task task) {
        try {
            return floatGithubProjectConverter.getRepositories(task.getProjectName());
        } catch (IOException | NoMatchFoundException e) {
            // ignore
        }
        return Collections.emptyList();
    }

    private Observable<Task> getTasksFor(String floatUsername) {
        return peopleServiceClient.getPersons()
                .filter(person -> person.getName().equalsIgnoreCase(floatUsername))
                .flatMap(person -> taskServiceClient.getTasks("2014-01-01", 200, person.getId())); // FIXME
    }
}
