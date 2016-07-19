package com.novoda.github.reports.stats.handler;

import com.novoda.github.reports.data.DataLayerException;
import com.novoda.github.reports.data.EventDataLayer;
import com.novoda.github.reports.data.model.UserAssignments;
import com.novoda.github.reports.data.model.UserAssignmentsStats;
import com.novoda.github.reports.stats.command.OverallOptions;

import java.util.*;
import java.util.stream.Collectors;

public class OverallCommandHandler implements CommandHandler<UserAssignmentsStats, OverallOptions> {

    private final EventDataLayer eventDataLayer;

    public OverallCommandHandler(EventDataLayer eventDataLayer) {
        this.eventDataLayer = eventDataLayer;
    }

    @Override
    public UserAssignmentsStats handle(OverallOptions options) {

        // TODO: get the real assignments using float
        UserAssignments ptAssignment = UserAssignments.builder()
                .assignedRepositories(Collections.singletonList("github-reports"))
                .assignmentStart(new GregorianCalendar(2016, 0, 1).getTime())
                .assignmentEnd(new GregorianCalendar(2016, 6, 31).getTime())
                .build();

        Map<String, List<UserAssignments>> usersAssignments = options.getUsers()
                .stream()
                .collect(Collectors.toMap(
                        user -> user,
                        // TODO: set the real assignments here
                        user -> Collections.singletonList(ptAssignment)
                ));

        try {
            return eventDataLayer.getUserAssignmentsStats(usersAssignments);
        } catch (DataLayerException e) {
            e.printStackTrace();
        }

        return null;
    }

}
