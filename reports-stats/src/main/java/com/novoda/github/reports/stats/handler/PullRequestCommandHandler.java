package com.novoda.github.reports.stats.handler;

import com.novoda.floatschedule.convert.FloatGithubUserConverter;
import com.novoda.github.reports.data.DataLayerException;
import com.novoda.github.reports.data.EventDataLayer;
import com.novoda.github.reports.data.model.PullRequestStats;
import com.novoda.github.reports.stats.command.PullRequestOptions;
import com.novoda.github.reports.stats.command.PullRequestOptionsGroupBy;

import java.io.IOException;
import java.util.List;

public class PullRequestCommandHandler implements CommandHandler<PullRequestStats, PullRequestOptions> {

    private final EventDataLayer eventDataLayer;
    private final FloatGithubUserConverter floatGithubUserConverter;

    public PullRequestCommandHandler(EventDataLayer eventDataLayer, FloatGithubUserConverter floatGithubUserConverter) {
        this.eventDataLayer = eventDataLayer;
        this.floatGithubUserConverter = floatGithubUserConverter;
    }

    @Override
    public PullRequestStats handle(PullRequestOptions options) {

        try {
            List<String> users = getUsersFromOptionsOrAll(options);

            return eventDataLayer.getOrganisationStats(
                    options.getFrom(),
                    options.getTo(),
                    options.getRepositories(),
                    users,
                    convertToGroupBy(options.getGroupBy()),
                    options.withAverage()
            );
        } catch (DataLayerException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private List<String> getUsersFromOptionsOrAll(PullRequestOptions options) throws IOException {
        List<String> users = options.getUsers();
        if (isListNullOrEmpty(users)) {
            users = floatGithubUserConverter.getGithubUsers();
        }
        return users;
    }

    private boolean isListNullOrEmpty(List<String> users) {
        return users == null || users.isEmpty();
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
