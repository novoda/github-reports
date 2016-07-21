package com.novoda.github.reports.stats.handler;

import com.novoda.floatschedule.FloatServiceClient;
import com.novoda.floatschedule.convert.FloatDateConverter;
import com.novoda.floatschedule.task.Task;
import com.novoda.github.reports.data.DataLayerException;
import com.novoda.github.reports.data.EventDataLayer;
import com.novoda.github.reports.data.model.UserAssignments;
import com.novoda.github.reports.data.model.UserAssignmentsStats;
import com.novoda.github.reports.stats.command.OverallOptions;
import rx.functions.Action2;
import rx.functions.Func1;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class OverallCommandHandler implements CommandHandler<UserAssignmentsStats, OverallOptions> {

    private final EventDataLayer eventDataLayer;
    private final FloatServiceClient floatServiceClient;
    private final FloatDateConverter floatDateConverter;

    public OverallCommandHandler(EventDataLayer eventDataLayer,
                                 FloatServiceClient floatServiceClient,
                                 FloatDateConverter floatDateConverter) {

        this.eventDataLayer = eventDataLayer;
        this.floatServiceClient = floatServiceClient;
        this.floatDateConverter = floatDateConverter;
    }

    @Override
    public UserAssignmentsStats handle(OverallOptions options) {
        Map<String, List<UserAssignments>> usersAssignments = floatServiceClient
                .getTasksForGithubUsers(options.getUsers(), options.getFrom(), options.getTo())
                .map(tasksToUserAssignments())
                .collect(
                        HashMap<String, List<UserAssignments>>::new,
                        putEntryInMap()
                )
                .toBlocking()
                .first();

        try {
            return eventDataLayer.getUserAssignmentsStats(usersAssignments);
        } catch (DataLayerException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Func1<Map.Entry<String, List<Task>>, AbstractMap.SimpleImmutableEntry<String, List<UserAssignments>>> tasksToUserAssignments() {
        return entry -> {
            String key = entry.getKey();
            List<UserAssignments> assignments = entry.getValue()
                    .stream()
                    .map(taskToUserAssignments())
                    .collect(Collectors.toList());
            return new AbstractMap.SimpleImmutableEntry<>(key, assignments);
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
                    .assignedRepositories(repositoriesForTask)
                    .build();
        };
    }

    private Action2<HashMap<String, List<UserAssignments>>, AbstractMap.SimpleImmutableEntry<String, List<UserAssignments>>> putEntryInMap() {
        return (map, entry) -> map.put(entry.getKey(), entry.getValue());
    }

}
