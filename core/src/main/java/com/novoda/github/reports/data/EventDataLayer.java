package com.novoda.github.reports.data;

import com.novoda.github.reports.data.model.Event;
import com.novoda.github.reports.data.model.PullRequestStats;
import com.novoda.github.reports.data.model.UserAssignments;
import com.novoda.github.reports.data.model.UserAssignmentsStats;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface EventDataLayer extends DataLayer<Event> {

    PullRequestStats getStats(Date from,
                              Date to,
                              List<String> repositories,
                              List<String> organisationUsers,
                              List<String> assignedUsers,
                              PullRequestStatsGroupBy groupBy,
                              Boolean withAverage) throws DataLayerException;

    PullRequestStats getOrganisationStats(Date from,
                                          Date to,
                                          List<String> repositories,
                                          List<String> organisationUsers,
                                          PullRequestStatsGroupBy groupBy,
                                          Boolean withAverage) throws DataLayerException;

    enum PullRequestStatsGroupBy {
        NONE,
        WEEK,
        MONTH
    }

    UserAssignmentsStats getUserAssignmentsStats(Map<String, List<UserAssignments>> usersAssignments)
            throws DataLayerException;

}

