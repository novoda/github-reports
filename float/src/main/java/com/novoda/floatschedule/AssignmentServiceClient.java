package com.novoda.floatschedule;

import com.novoda.floatschedule.convert.FloatGithubProjectConverter;
import com.novoda.floatschedule.convert.FloatGithubUserConverter;
import com.novoda.floatschedule.task.Task;
import com.novoda.floatschedule.task.TaskServiceClient;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import rx.Observable;
import rx.functions.Func1;

public class AssignmentServiceClient {

    private static final String NOVODA_BIG_BANG = "YYYY-MM-dd";
    private static final int NUMBER_OF_WEEKS = 0;
    private static final Integer NO_PERSON_ID = null;

    private final TaskServiceClient taskServiceClient;
    private final FloatGithubUserConverter floatGithubUserConverter;
    private final FloatGithubProjectConverter floatGithubProjectConverter;

    AssignmentServiceClient(TaskServiceClient taskServiceClient,
                            FloatGithubUserConverter floatGithubUserConverter,
                            FloatGithubProjectConverter floatGithubProjectConverter) {

        this.taskServiceClient = taskServiceClient;
        this.floatGithubUserConverter = floatGithubUserConverter;
        this.floatGithubProjectConverter = floatGithubProjectConverter;
    }

    public Observable<String> getGithubUsernamesAssignedToRepositories(List<String> repositoryNames) {
        List<String> floatProjectNames = getFloatProjectNamesFrom(repositoryNames);
        return getGithubUsernamesAssignedToProjects(floatProjectNames);
    }

    private List<String> getFloatProjectNamesFrom(List<String> repositoryNames) {
        return repositoryNames.stream()
                .map(this::getFloatProjectName)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private Optional<String> getFloatProjectName(String repositoryName) {
        try {
            return Optional.of(floatGithubProjectConverter.getFloatProject(repositoryName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Observable<String> getGithubUsernamesAssignedToProjects(List<String> floatProjectNames) {
            return taskServiceClient.getTasks(NOVODA_BIG_BANG, NUMBER_OF_WEEKS, NO_PERSON_ID)
                    .filter(byProjectNameIn(floatProjectNames))
                    .map(Task::getPersonName)
                    .map(this::toGithubUsername)
                    .filter(notNull())
                    .distinct();
    }

    private Func1<Task, Boolean> byProjectNameIn(List<String> floatProjectNames) {
        return task -> floatProjectNames.contains(task.getProjectName());
    }

    private String toGithubUsername(String floatUsername) {
        try {
            return floatGithubUserConverter.getGithubUser(floatUsername);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Func1<String, Boolean> notNull() {
        return githubUsername -> githubUsername != null;
    }

}
