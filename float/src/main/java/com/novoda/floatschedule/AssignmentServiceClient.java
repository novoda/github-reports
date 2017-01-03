package com.novoda.floatschedule;

import com.novoda.floatschedule.convert.FailedToLoadMappingsException;
import com.novoda.floatschedule.convert.SheetsFloatGithubProjectConverter;
import com.novoda.floatschedule.convert.SheetsFloatGithubUserConverter;
import com.novoda.floatschedule.task.Task;
import com.novoda.floatschedule.task.TaskServiceClient;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import rx.Observable;
import rx.functions.Func1;

public class AssignmentServiceClient {

    private static final Integer NO_PERSON_ID = null;

    private final TaskServiceClient taskServiceClient;
    private final SheetsFloatGithubUserConverter floatGithubUserConverter;
    private final SheetsFloatGithubProjectConverter floatGithubProjectConverter;

    public static AssignmentServiceClient newInstance() {
        TaskServiceClient taskServiceClient = TaskServiceClient.newInstance();
        SheetsFloatGithubUserConverter floatGithubUserConverter = SheetsFloatGithubUserConverter.newInstance();
        SheetsFloatGithubProjectConverter floatGithubProjectConverter = SheetsFloatGithubProjectConverter.newInstance();
        return new AssignmentServiceClient(taskServiceClient, floatGithubUserConverter, floatGithubProjectConverter);
    }

    private AssignmentServiceClient(TaskServiceClient taskServiceClient,
                                    SheetsFloatGithubUserConverter floatGithubUserConverter,
                                    SheetsFloatGithubProjectConverter floatGithubProjectConverter) {

        this.taskServiceClient = taskServiceClient;
        this.floatGithubUserConverter = floatGithubUserConverter;
        this.floatGithubProjectConverter = floatGithubProjectConverter;
    }

    public Observable<String> getGithubUsernamesAssignedToRepositories(List<String> repositoryNames,
                                                                       Date startDate,
                                                                       int numberOfWeeks,
                                                                       TimeZone timezone) {

        List<String> floatProjectNames = getFloatProjectNamesFrom(repositoryNames);
        return getGithubUsernamesAssignedToProjects(floatProjectNames, startDate, numberOfWeeks, timezone);
    }

    private List<String> getFloatProjectNamesFrom(List<String> repositoryNames) {
        return repositoryNames.stream()
                .flatMap(repositoryName -> getFloatProjectNames(repositoryName).stream())
                .distinct()
                .collect(Collectors.toList());
    }

    private List<String> getFloatProjectNames(String repositoryName) {
        try {
            return floatGithubProjectConverter.getFloatProjects(repositoryName);
        } catch (FailedToLoadMappingsException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public Observable<String> getGithubUsernamesAssignedToProjects(List<String> floatProjectNames,
                                                                   Date startDate,
                                                                   int numberOfWeeks,
                                                                   TimeZone timezone) {

        return taskServiceClient.getTasks(startDate, numberOfWeeks, timezone, NO_PERSON_ID)
                .filter(byProjectNameIn(floatProjectNames))
                .map(Task::getPersonName)
                .map(toGithubUsernameOrNull())
                .filter(notNull())
                .distinct();
    }

    private Func1<Task, Boolean> byProjectNameIn(List<String> floatProjectNames) {
        return task -> floatProjectNames.contains(task.getProjectName());
    }

    private Func1<String, String> toGithubUsernameOrNull() {
        return floatUsername -> {
            try {
                return floatGithubUserConverter.getGithubUser(floatUsername);
            } catch (FailedToLoadMappingsException e) {
                e.printStackTrace();
                return null;
            }
        };
    }

    private Func1<String, Boolean> notNull() {
        return string -> string != null;
    }
}
