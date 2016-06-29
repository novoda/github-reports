package com.novoda.github.reports.stats.handler;

import com.novoda.github.reports.data.DataLayerException;
import com.novoda.github.reports.data.EventDataLayer;
import com.novoda.github.reports.data.db.DbEventDataLayer;
import com.novoda.github.reports.data.model.PullRequestStats;
import com.novoda.github.reports.stats.command.PullRequestOptions;
import com.novoda.github.reports.stats.command.PullRequestOptionsGroupBy;

import java.util.List;

public class PullRequestCommandHandler implements CommandHandler<PullRequestStats, PullRequestOptions> {

    private final DbEventDataLayer dbEventDataLayer;

    public PullRequestCommandHandler(DbEventDataLayer dbEventDataLayer) {
        this.dbEventDataLayer = dbEventDataLayer;
    }

    @Override
    public PullRequestStats handle(PullRequestOptions options) {
        // TODO: retrieve repositories from float projects, for now it works only with explicit repos
        List<String> repositoriesFromProjects = options.getRepositories();

        try {
            return dbEventDataLayer.getStats(
                    options.getFrom(),
                    options.getTo(),
                    repositoriesFromProjects,
                    options.getTeamUsers(),
                    options.getAssignedUsers(),
                    options.getFilterUsers(),
                    convertToGroupBy(options.getGroupBy()),
                    options.withAverage()
            );
        } catch (DataLayerException e) {
            e.printStackTrace();
        }

        return null;
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
