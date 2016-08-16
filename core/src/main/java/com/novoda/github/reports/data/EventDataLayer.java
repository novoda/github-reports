package com.novoda.github.reports.data;

import com.novoda.github.reports.data.model.*;

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

    UserAssignmentsStats getUserAssignmentsStats(Date from,
                                                 Date to,
                                                 Map<String, List<UserAssignments>> usersAssignments)
            throws DataLayerException;

    AggregatedStats getAggregatedUserAssignmentsStats(Date from,
                                                      Date to,
                                                      Map<String, List<UserAssignments>> usersAssignments)
            throws DataLayerException;

    enum PullRequestStatsGroupBy {
        NONE,
        WEEK,
        MONTH
    }

}

