package com.novoda.github.reports.stats.handler;

import com.novoda.github.reports.data.EventDataLayer;
import com.novoda.github.reports.data.db.DbEventDataLayer;
import com.novoda.github.reports.data.model.PullRequestStats;
import com.novoda.github.reports.stats.command.PullRequestOptions;
import com.novoda.github.reports.stats.command.PullRequestOptionsGroupBy;

public class PullRequestCommandHandler implements CommandHandler<PullRequestStats, PullRequestOptions> {

    private final DbEventDataLayer dbEventDataLayer;

    public PullRequestCommandHandler(DbEventDataLayer dbEventDataLayer) {
        this.dbEventDataLayer = dbEventDataLayer;
    }

    @Override
    public PullRequestStats handle(PullRequestOptions options) {
        return dbEventDataLayer.getStats(
                options.getFrom(),
                options.getTo(),
                options.getProjects(),
                options.getRepositories(),
                options.getUsers(),
                convertToGroupBy(options.getGroupBy()),
                options.withAverage()
        );
    }

    private EventDataLayer.PullRequestStatsGroupBy convertToGroupBy(PullRequestOptionsGroupBy groupBy) {
        if (groupBy == PullRequestOptionsGroupBy.MONTH) {
            return EventDataLayer.PullRequestStatsGroupBy.MONTH;
        }
        if (groupBy == PullRequestOptionsGroupBy.WEEK) {
            return EventDataLayer.PullRequestStatsGroupBy.WEEK;
        }
        return EventDataLayer.PullRequestStatsGroupBy.NONE;
    }

}
