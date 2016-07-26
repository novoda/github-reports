package com.novoda.github.reports.stats.handler;

import com.novoda.floatschedule.FloatServiceClient;
import com.novoda.github.reports.data.DataLayerException;
import com.novoda.github.reports.data.EventDataLayer;
import com.novoda.github.reports.data.model.AggregatedStats;
import com.novoda.github.reports.data.model.UserAssignments;
import com.novoda.github.reports.stats.command.AggregateOptions;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class AggregateCommandHandler extends FloatTaskBasedCommandHandler<AggregatedStats, AggregateOptions> {

    public AggregateCommandHandler(EventDataLayer eventDataLayer,
                                   FloatServiceClient floatServiceClient) {

        super(eventDataLayer, floatServiceClient);
    }

    @Override
    protected AggregatedStats handleUserAssignments(Date from,
                                                    Date to,
                                                    Map<String, List<UserAssignments>> usersAssignments)
            throws DataLayerException {

        return getEventDataLayer().getAggregatedUserAssignmentsStats(from, to, usersAssignments);
    }

}
