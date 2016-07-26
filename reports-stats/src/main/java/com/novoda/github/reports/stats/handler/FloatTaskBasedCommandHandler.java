package com.novoda.github.reports.stats.handler;

import com.novoda.floatschedule.FloatServiceClient;
import com.novoda.floatschedule.convert.FloatDateConverter;
import com.novoda.github.reports.data.DataLayerException;
import com.novoda.github.reports.data.EventDataLayer;
import com.novoda.github.reports.data.model.Stats;
import com.novoda.github.reports.data.model.UserAssignments;
import com.novoda.github.reports.reader.UsersServiceClient;
import com.novoda.github.reports.stats.command.FloatTaskBasedOptions;

import java.util.List;
import java.util.Map;

abstract class FloatTaskBasedCommandHandler<S extends Stats, O extends FloatTaskBasedOptions>
        implements CommandHandler<S, O> {

    private final EventDataLayer eventDataLayer;
    private final FloatServiceClient floatServiceClient;
    private final FloatDateConverter floatDateConverter;
    private final UsersServiceClient usersServiceClient;

    FloatTaskBasedCommandHandler(EventDataLayer eventDataLayer,
                                 FloatServiceClient floatServiceClient,
                                 FloatDateConverter floatDateConverter,
                                 UsersServiceClient usersServiceClient) {

        this.eventDataLayer = eventDataLayer;
        this.floatServiceClient = floatServiceClient;
        this.floatDateConverter = floatDateConverter;
        this.usersServiceClient = usersServiceClient;
    }

    @Override
    public S handle(O options) {
        Map<String, List<UserAssignments>> usersAssignments = floatServiceClient.getGithubUsersAssignmentsInDateRange(
                options.getUsers(),
                options.getFrom(),
                options.getTo()
        );

        try {
            return handleUserAssignments(usersAssignments);
        } catch (DataLayerException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected abstract S handleUserAssignments(Map<String, List<UserAssignments>> usersAssignments)
            throws DataLayerException;

    EventDataLayer getEventDataLayer() {
        return eventDataLayer;
    }
}
