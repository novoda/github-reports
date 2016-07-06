package com.novoda.github.reports.data;

import com.novoda.github.reports.data.model.Event;
import com.novoda.github.reports.data.model.PullRequestStats;

import java.util.Date;
import java.util.List;

public interface EventDataLayer extends DataLayer<Event> {

    PullRequestStats getStats(Date from,
                              Date to,
                              List<String> repositories,
                              List<String> organisationUsers,
                              List<String> assignedUsers,
                              PullRequestStatsGroupBy groupBy,
                              boolean withAverage) throws DataLayerException;

    PullRequestStats getOrganisationStats(Date from,
                                          Date to,
                                          List<String> repositories,
                                          List<String> organisationUsers,
                                          PullRequestStatsGroupBy groupBy,
                                          boolean withAverage) throws DataLayerException;

    enum PullRequestStatsGroupBy {
        NONE,
        WEEK,
        MONTH
    }

}

