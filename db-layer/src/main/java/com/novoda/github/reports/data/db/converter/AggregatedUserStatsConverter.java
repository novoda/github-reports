package com.novoda.github.reports.data.db.converter;

import com.novoda.github.reports.data.model.AggregatedStats;
import com.novoda.github.reports.data.model.AggregatedUserStats;
import org.jooq.Record;
import org.jooq.Result;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static com.novoda.github.reports.data.db.builder.EventUserAssignmentsQueryBuilder.*;

public class AggregatedUserStatsConverter {

    public AggregatedStats convert(Map<String, ? extends Result<? extends Record>> resultsGroupedByUsername) {
        Map<String, AggregatedUserStats> usersStats = resultsGroupedByUsername.entrySet()
                .stream()
                .map(userRecordToAggregatedUserStats())
                .collect(toMap());

        return AggregatedStats.builder()
                .usersStats(usersStats)
                .build();
    }

    private Function<Map.Entry<String, ? extends Result<? extends Record>>, SimpleImmutableEntry<String, AggregatedUserStats>> userRecordToAggregatedUserStats() {
        return userStats -> {
            String username = userStats.getKey();
            AggregatedUserStats aggregatedUserStats = userStats
                    .getValue()
                    .intoGroups(WAS_SCHEDULED_WORK_FIELD)
                    .entrySet()
                    .stream()
                    .map(assignedOrExternalRecordsToMapEntries())
                    .collect(
                            AggregatedUserStats::builder,
                            mapEntriesIntoAggregatedUserStats(),
                            noopMerger()
                    )
                    .build();

            return new SimpleImmutableEntry<>(username, aggregatedUserStats);
        };
    }

    private Function<Map.Entry<Boolean, ? extends Result<? extends Record>>, SimpleImmutableEntry<Boolean, Map<String, Integer>>> assignedOrExternalRecordsToMapEntries() {
        return assignedOrExternalEntries -> {
            Boolean isAssigned = assignedOrExternalEntries.getKey();
            Map<String, Integer> projectSetStats = assignedOrExternalEntries.getValue()
                    .intoGroups(PROJECT_ASSIGNED_FIELD)
                    .entrySet()
                    .stream()
                    .map(projectRecordsToProjectCount())
                    .collect(toMap());
            return new SimpleImmutableEntry<>(isAssigned, projectSetStats);
        };
    }

    private Function<Map.Entry<String, ? extends Result<? extends Record>>, SimpleImmutableEntry<String, Integer>> projectRecordsToProjectCount() {
        return projectStats -> {
            Integer count = projectStats
                    .getValue()
                    .getValues(COUNT_EVENT_FIELD)
                    .stream()
                    .collect(Collectors.summingInt(value -> value));
            return new SimpleImmutableEntry<>(projectStats.getKey(), count);
        };
    }

    private <K, V> Collector<SimpleImmutableEntry<K, V>, ?, Map<K, V>> toMap() {
        return Collectors.toMap(SimpleImmutableEntry::getKey, SimpleImmutableEntry::getValue);
    }

    private BiConsumer<AggregatedUserStats.Builder, SimpleImmutableEntry<Boolean, Map<String, Integer>>> mapEntriesIntoAggregatedUserStats() {
        return (builder, assignedOrExternalStats) -> {
            Boolean isAssigned = assignedOrExternalStats.getKey();
            Map<String, Integer> projectsStats = assignedOrExternalStats.getValue();
            Integer projectsTotalContributions = sumMapValues(projectsStats);

            if (isAssigned) {
                builder.assignedProjectsStats(projectsStats);
                builder.assignedProjectsContributions(projectsTotalContributions);
            } else {
                builder.externalProjectsStats(projectsStats);
                builder.externalProjectsContributions(projectsTotalContributions);
            }
        };
    }

    private Integer sumMapValues(Map<String, Integer> projectsStats) {
        return projectsStats
                .entrySet()
                .stream()
                .collect(Collectors.summingInt(Map.Entry::getValue));
    }

    private BiConsumer<AggregatedUserStats.Builder, AggregatedUserStats.Builder> noopMerger() {
        return (fromBuilder, toBuilder) -> {
        };
    }

}
