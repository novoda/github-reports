package com.novoda.github.reports.stats.handler;

import com.novoda.floatschedule.FloatServiceClient;
import com.novoda.github.reports.data.DataLayerException;
import com.novoda.github.reports.data.EventDataLayer;
import com.novoda.github.reports.data.model.Stats;
import com.novoda.github.reports.data.model.UserAssignments;
import com.novoda.github.reports.stats.command.FloatTaskBasedOptions;

import java.util.Date;
import java.util.List;
import java.util.Map;

abstract class FloatTaskBasedCommandHandler<S extends Stats, O extends FloatTaskBasedOptions>
        implements CommandHandler<S, O> {

    private final EventDataLayer eventDataLayer;
    private final FloatServiceClient floatServiceClient;

    FloatTaskBasedCommandHandler(EventDataLayer eventDataLayer,
                                 FloatServiceClient floatServiceClient) {

        this.eventDataLayer = eventDataLayer;
        this.floatServiceClient = floatServiceClient;
    }

    @Override
    public S handle(O options) {
        Map<String, List<UserAssignments>> usersAssignments = floatServiceClient.getGithubUsersAssignmentsInDateRange(
                options.getUsers(),
                options.getFrom(),
                options.getTo(),
                options.getTimezone()
        );

        try {
            return handleUserAssignments(options.getFrom(), options.getTo(), usersAssignments);
        } catch (DataLayerException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected abstract S handleUserAssignments(Date from,
                                               Date to,
                                               Map<String, List<UserAssignments>> usersAssignments)
            throws DataLayerException;

    EventDataLayer getEventDataLayer() {
        return eventDataLayer;
    }
}
