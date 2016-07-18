package com.novoda.github.reports.stats.handler;

import com.novoda.github.reports.data.EventDataLayer;
import com.novoda.github.reports.data.model.UserAssignment;
import com.novoda.github.reports.data.model.UserAssignmentsStats;
import com.novoda.github.reports.data.model.UserContribution;
import com.novoda.github.reports.stats.command.OverallOptions;

import java.math.BigDecimal;
import java.util.*;

public class OverallCommandHandler implements CommandHandler<UserAssignmentsStats, OverallOptions> {

    private final EventDataLayer eventDataLayer;

    public OverallCommandHandler(EventDataLayer eventDataLayer) {
        this.eventDataLayer = eventDataLayer;
    }

    @Override
    public UserAssignmentsStats handle(OverallOptions options) {
        // TODO: replace with actual query

        UserContribution bananaContribution = UserContribution.builder()
                .project("banana")
                .comments(BigDecimal.TEN)
                .openedPullRequests(BigDecimal.TEN)
                .closedPullRequests(BigDecimal.TEN)
                .mergedPullRequests(BigDecimal.TEN)
                .openedIssues(BigDecimal.ZERO)
                .closedIssues(BigDecimal.ZERO)
                .build();

        UserContribution anotherContribution = UserContribution.builder()
                .project("another")
                .comments(BigDecimal.TEN)
                .openedPullRequests(BigDecimal.TEN)
                .closedPullRequests(BigDecimal.TEN)
                .mergedPullRequests(BigDecimal.TEN)
                .openedIssues(BigDecimal.ZERO)
                .closedIssues(BigDecimal.ZERO)
                .build();

        UserAssignment bananaAssignment = UserAssignment.builder()
                .assignedProject("banana")
                .assignmentStart(new GregorianCalendar(2016, 0, 1).getTime())
                .assignmentEnd(new GregorianCalendar(2016, 5, 30).getTime())
                .contributions(Arrays.asList(bananaContribution, anotherContribution))
                .build();

        UserAssignment anotherAssignment = UserAssignment.builder()
                .assignedProject("another")
                .assignmentStart(new GregorianCalendar(2016, 6, 1).getTime())
                .contributions(Collections.singletonList(anotherContribution))
                .build();

        Map<String, List<UserAssignment>> assignments = new HashMap<>();
        assignments.put("frapontillo", Arrays.asList(bananaAssignment, anotherAssignment));
        assignments.put("takecare", Collections.singletonList(bananaAssignment));

        return UserAssignmentsStats.builder()
                .userAssignments(assignments)
                .build();
    }

}
