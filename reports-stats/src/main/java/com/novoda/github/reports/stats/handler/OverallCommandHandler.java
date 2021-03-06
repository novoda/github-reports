package com.novoda.github.reports.stats.handler;

import com.novoda.floatschedule.FloatServiceClient;
import com.novoda.github.reports.data.DataLayerException;
import com.novoda.github.reports.data.EventDataLayer;
import com.novoda.github.reports.data.model.UserAssignments;
import com.novoda.github.reports.data.model.UserAssignmentsStats;
import com.novoda.github.reports.stats.command.OverallOptions;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class OverallCommandHandler extends FloatTaskBasedCommandHandler<UserAssignmentsStats, OverallOptions> {

    public OverallCommandHandler(EventDataLayer eventDataLayer,
                                 FloatServiceClient floatServiceClient) {

        super(eventDataLayer, floatServiceClient);
    }


    @Override
    protected UserAssignmentsStats handleUserAssignments(Date from,
                                                         Date to,
                                                         Map<String, List<UserAssignments>> usersAssignments)
            throws DataLayerException {

        return getEventDataLayer().getUserAssignmentsStats(from, to, usersAssignments);
    }

}
