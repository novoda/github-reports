package com.novoda.github.reports.stats.handler;

import com.novoda.floatschedule.FloatServiceClient;
import com.novoda.floatschedule.convert.FloatDateConverter;
import com.novoda.github.reports.data.EventDataLayer;
import com.novoda.github.reports.data.model.AggregatedStats;
import com.novoda.github.reports.data.model.AggregatedUserStats;
import com.novoda.github.reports.stats.command.AggregateOptions;

import java.util.HashMap;
import java.util.Map;

public class AggregateCommandHandler implements CommandHandler<AggregatedStats, AggregateOptions> {

    private final EventDataLayer eventDataLayer;
    private final FloatServiceClient floatServiceClient;
    private final FloatDateConverter floatDateConverter;

    public AggregateCommandHandler(EventDataLayer eventDataLayer,
                                   FloatServiceClient floatServiceClient,
                                   FloatDateConverter floatDateConverter) {

        this.eventDataLayer = eventDataLayer;
        this.floatServiceClient = floatServiceClient;
        this.floatDateConverter = floatDateConverter;
    }

    @Override
    public AggregatedStats handle(AggregateOptions options) {

        // TODO: fetch real data from database

        Map<String, Integer> anyProjectsStats = new HashMap<>();
        anyProjectsStats.put("R & D", 12);
        anyProjectsStats.put("All 4", 1);
        anyProjectsStats.put("Open Source", 2);

        AggregatedUserStats anyUserStats = AggregatedUserStats.builder()
                .assignedProjectsContributions(120)
                .externalProjectsContributions(55)
                .assignedProjectsStats(anyProjectsStats)
                .externalProjectsStats(anyProjectsStats)
                .build();

        Map<String, AggregatedUserStats> userStats = new HashMap<>();
        userStats.put("frapontillo", anyUserStats);
        userStats.put("takecare", anyUserStats);

        return AggregatedStats.builder()
                .userStats(userStats)
                .build();
    }

}
