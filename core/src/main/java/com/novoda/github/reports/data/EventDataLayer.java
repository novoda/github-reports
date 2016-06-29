package com.novoda.github.reports.data;

import com.novoda.github.reports.data.model.Event;
import com.novoda.github.reports.data.model.PullRequestStats;

import java.util.Date;
import java.util.List;

public interface EventDataLayer extends DataLayer<Event> {

    PullRequestStats getStats(Date from,
                              Date to,
                              List<String> repositories,
                              List<String> teamUsers,
                              List<String> projectUsers,
                              List<String> users,
                              PullRequestStatsGroupBy groupBy,
                              boolean withAverage) throws DataLayerException;

    enum PullRequestStatsGroupBy {
        NONE,
        WEEK,
        MONTH
    }

}

