package com.novoda.github.reports.stats.handler;

import com.novoda.floatschedule.FloatServiceClient;
import com.novoda.floatschedule.convert.FloatDateConverter;
import com.novoda.floatschedule.task.Task;
import com.novoda.github.reports.data.DataLayerException;
import com.novoda.github.reports.data.EventDataLayer;
import com.novoda.github.reports.data.model.Stats;
import com.novoda.github.reports.data.model.UserAssignments;
import com.novoda.github.reports.reader.UsersServiceClient;
import com.novoda.github.reports.stats.command.FloatTaskBasedOptions;
import rx.functions.Action2;
import rx.functions.Func1;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

abstract class FloatTaskBasedCommandHandler<S extends Stats, O extends FloatTaskBasedOptions>
        implements CommandHandler<S, O> {

    private final EventDataLayer eventDataLayer;
    private final FloatServiceClient floatServiceClient;
    private final FloatDateConverter floatDateConverter;
    private final UsersServiceClient usersServiceClient;

    FloatTaskBasedCommandHandler(EventDataLayer eventDataLayer,
                                 FloatServiceClient floatServiceClient,
                                 FloatDateConverter floatDateConverter,
                                 UsersServiceClient usersServiceClient) {

        this.eventDataLayer = eventDataLayer;
        this.floatServiceClient = floatServiceClient;
        this.floatDateConverter = floatDateConverter;
        this.usersServiceClient = usersServiceClient;
    }

    @Override
    public S handle(O options) {
        List<String> githubUsers = options.getUsers();
        if (listIsNullOrEmpty(githubUsers)) {
            githubUsers = usersServiceClient.getAllGithubUsers()
                    .toList()
                    .toBlocking()
                    .first();
        }

        Map<String, List<UserAssignments>> usersAssignments = getUsersAssignmentsInDateRange(options, githubUsers);

        try {
            return handleUserAssignments(usersAssignments);
        } catch (DataLayerException e) {
            e.printStackTrace();
        }

        return null;
    }

    private boolean listIsNullOrEmpty(List<String> githubUsers) {
        return githubUsers == null || githubUsers.isEmpty();
    }

    private HashMap<String, List<UserAssignments>> getUsersAssignmentsInDateRange(O options, List<String> githubUsers) {
        return floatServiceClient
                .getTasksForGithubUsers(githubUsers, options.getFrom(), options.getTo())
                .map(tasksToUserAssignments())
                .collect(
                        HashMap<String, List<UserAssignments>>::new,
                        putEntryInMap()
                )
                .toBlocking()
                .first();
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

    private Function<Task, UserAssignments> taskToUserAssignments() {
        return task -> {
            Date assignmentStart = floatDateConverter.fromFloatDateFormatOrNoDate(task.getStartDate());
            Date assignmentEnd = floatDateConverter.fromFloatDateFormatOrNoDate(task.getEndDate());
            List<String> repositoriesForTask = floatServiceClient.getRepositoriesFor(task);

            return UserAssignments.builder()
                    .assignmentStart(assignmentStart)
                    .assignmentEnd(assignmentEnd)
                    .assignedProject(task.getProjectName())
                    .assignedRepositories(repositoriesForTask)
                    .build();
        };
    }

    protected abstract S handleUserAssignments(Map<String, List<UserAssignments>> usersAssignments)
            throws DataLayerException;

    private Action2<HashMap<String, List<UserAssignments>>, AbstractMap.SimpleImmutableEntry<String, List<UserAssignments>>> putEntryInMap() {
        return (map, entry) -> map.put(entry.getKey(), entry.getValue());
    }

    EventDataLayer getEventDataLayer() {
        return eventDataLayer;
    }
}
