package com.novoda.github.reports.data;

import com.novoda.github.reports.data.model.Event;
import com.novoda.github.reports.data.model.PullRequestStats;

import java.util.Date;
import java.util.List;

public interface EventDataLayer extends DataLayer<Event> {

    PullRequestStats getStats(Date from,
                              Date to,
                              List<String> projects,
                              List<String> repositories,
                              List<String> users,
                              PullRequestStatsGroupBy groupBy,
                              boolean withAverage);

    enum PullRequestStatsGroupBy {
        NONE,
        WEEK,
        MONTH
    }

}

