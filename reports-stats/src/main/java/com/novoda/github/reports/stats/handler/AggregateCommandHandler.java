package com.novoda.github.reports.stats.handler;

import com.novoda.floatschedule.FloatServiceClient;
import com.novoda.floatschedule.convert.FloatDateConverter;
import com.novoda.github.reports.data.DataLayerException;
import com.novoda.github.reports.data.EventDataLayer;
import com.novoda.github.reports.data.model.AggregatedStats;
import com.novoda.github.reports.data.model.UserAssignments;
import com.novoda.github.reports.stats.command.AggregateOptions;

import java.util.List;
import java.util.Map;

public class AggregateCommandHandler extends FloatTaskBasedCommandHandler<AggregatedStats, AggregateOptions> {

    public AggregateCommandHandler(EventDataLayer eventDataLayer,
                                   FloatServiceClient floatServiceClient,
                                   FloatDateConverter floatDateConverter) {

        super(eventDataLayer, floatServiceClient, floatDateConverter);
    }

    @Override
    protected AggregatedStats handleUserAssignments(Map<String, List<UserAssignments>> usersAssignments)
            throws DataLayerException {

        return getEventDataLayer().getAggregatedUserAssignmentsStats(usersAssignments);
    }

}
