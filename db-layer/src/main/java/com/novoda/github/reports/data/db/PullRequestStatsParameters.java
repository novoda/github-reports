package com.novoda.github.reports.data.db;

import com.novoda.github.reports.data.EventDataLayer;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jooq.DSLContext;

public class PullRequestStatsParameters {

    private final DSLContext context;
    private final Date from;
    private final Date to;
    private final Set<String> repositories;
    private final Set<String> teamUsers;
    private final Set<String> assignedUsers;
    private final Set<String> filterUsers;
    private final EventDataLayer.PullRequestStatsGroupBy groupBy;
    private final boolean withAverage;

    public static PullRequestStatsParameters from(DSLContext context,
                                                  Date from,
                                                  Date to,
                                                  List<String> repositories,
                                                  List<String> teamUsers,
                                                  List<String> assignedUsers,
                                                  List<String> filterUsers,
                                                  EventDataLayer.PullRequestStatsGroupBy groupBy,
                                                  boolean withAverage) {

        Set<String> repositoriesSet = listToSet(repositories);
        Set<String> teamUsersSet = listToSet(teamUsers);
        Set<String> projectUsersSet = listToSet(assignedUsers);
        Set<String> filterUsersSet = listToSet(filterUsers);

        return new PullRequestStatsParameters(
                context,
                from,
                to,
                repositoriesSet,
                teamUsersSet,
                projectUsersSet,
                filterUsersSet,
                groupBy,
                withAverage
        );
    }

    private static Set<String> listToSet(List<String> list) {
        if (list == null) {
            return Collections.emptySet();
        }
        Set<String> set = new HashSet<>(list.size());
        set.addAll(list);
        return set;
    }

    public static PullRequestStatsParameters from(DSLContext context,
                                                  Date from,
                                                  Date to,
                                                  Set<String> repositories,
                                                  Set<String> teamUsers,
                                                  Set<String> projectUsers,
                                                  Set<String> filterUsers,
                                                  EventDataLayer.PullRequestStatsGroupBy groupBy,
                                                  boolean withAverage) {

        return new PullRequestStatsParameters(context, from, to, repositories, teamUsers, projectUsers, filterUsers, groupBy, withAverage);
    }

    private PullRequestStatsParameters(DSLContext context,
                                       Date from,
                                       Date to,
                                       Set<String> repositories,
                                       Set<String> teamUsers,
                                       Set<String> assignedUsers,
                                       Set<String> filterUsers,
                                       EventDataLayer.PullRequestStatsGroupBy groupBy,
                                       boolean withAverage) {

        this.context = context;
        this.from = from;
        this.to = to;
        this.repositories = repositories;
        this.teamUsers = teamUsers;
        this.assignedUsers = assignedUsers;
        this.filterUsers = filterUsers;
        this.groupBy = groupBy;
        this.withAverage = withAverage;
    }

    public DSLContext getContext() {
        return context;
    }

    public Date getFrom() {
        return from;
    }

    public Date getTo() {
        return to;
    }

    public Set<String> getRepositories() {
        return repositories;
    }

    public Set<String> getTeamUsers() {
        return teamUsers;
    }

    public Set<String> getAssignedUsers() {
        return assignedUsers;
    }

    public Set<String> getFilterUsers() {
        return filterUsers;
    }

    public EventDataLayer.PullRequestStatsGroupBy getGroupBy() {
        return groupBy;
    }

    public boolean isWithAverage() {
        return withAverage;
    }
}
