package com.novoda.github.reports.data.db.converter;

import com.novoda.github.reports.data.model.AggregatedStats;
import com.novoda.github.reports.data.model.AggregatedUserStats;
import org.jooq.Field;
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
        Map<String, AggregatedUserStats> usersStats = buildUserStatsMap(resultsGroupedByUsername);

        return AggregatedStats.builder()
                .usersStats(usersStats)
                .build();
    }

    private Map<String, AggregatedUserStats> buildUserStatsMap(Map<String, ? extends Result<? extends Record>> resultsGroupedByUsername) {
        return resultsGroupedByUsername.entrySet()
                .stream()
                .map(userRecordToAggregatedUserStats())
                .collect(toMap());
    }

    private Function<Map.Entry<String, ? extends Result<? extends Record>>, SimpleImmutableEntry<String, AggregatedUserStats>> userRecordToAggregatedUserStats() {
        return userStats -> {
            String username = userStats.getKey();
            AggregatedUserStats aggregatedUserStats = buildAggregatedUserStatsFromRecords(userStats.getValue());

            return new SimpleImmutableEntry<>(username, aggregatedUserStats);
        };
    }

    private AggregatedUserStats buildAggregatedUserStatsFromRecords(Result<? extends Record> userStats) {
        return userStats
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
    }

    private Function<Map.Entry<Boolean, ? extends Result<? extends Record>>, SimpleImmutableEntry<Boolean, Map<String, Integer>>> assignedOrExternalRecordsToMapEntries() {
        return assignedOrExternalEntries -> {
            Boolean isAssigned = assignedOrExternalEntries.getKey();
            Field<String> groupKey = getGroupingProjectOrRepositoryKey(isAssigned);

            Map<String, Integer> projectOrRepositorySetStats = buildProjectOrRepositorySetStatsFromRecords(
                    groupKey,
                    assignedOrExternalEntries.getValue()
            );

            return new SimpleImmutableEntry<>(isAssigned, projectOrRepositorySetStats);
        };
    }

    private Map<String, Integer> buildProjectOrRepositorySetStatsFromRecords(Field<String> groupKey, Result<? extends Record> records) {
        return records
                        .intoGroups(groupKey)
                        .entrySet()
                        .stream()
                        .map(projectOrRepositoryRecordsToCount())
                        .collect(toMap());
    }

    private Field<String> getGroupingProjectOrRepositoryKey(boolean isAssigned) {
        return isAssigned ? PROJECT_ASSIGNED_FIELD : REPOSITORY_WORKED_NAME_FIELD;
    }

    private Function<Map.Entry<String, ? extends Result<? extends Record>>, SimpleImmutableEntry<String, Integer>> projectOrRepositoryRecordsToCount() {
        return projectOrRepositoryStats -> {
            Integer count = sumCountField(projectOrRepositoryStats);
            return new SimpleImmutableEntry<>(projectOrRepositoryStats.getKey(), count);
        };
    }

    private Integer sumCountField(Map.Entry<String, ? extends Result<? extends Record>> projectOrRepositoryStats) {
        return projectOrRepositoryStats
                .getValue()
                .stream()
                .map(toCountField())
                .collect(toSum());
    }

    private Function<? super Record, Integer> toCountField() {
        return record -> record.getValue(COUNT_EVENT_FIELD);
    }

    private Collector<Integer, ?, Integer> toSum() {
        return Collectors.summingInt(value -> value);
    }

    private <K, V> Collector<SimpleImmutableEntry<K, V>, ?, Map<K, V>> toMap() {
        return Collectors.toMap(SimpleImmutableEntry::getKey, SimpleImmutableEntry::getValue);
    }

    private BiConsumer<AggregatedUserStats.Builder, SimpleImmutableEntry<Boolean, Map<String, Integer>>> mapEntriesIntoAggregatedUserStats() {
        return (builder, assignedOrExternalStats) -> {
            boolean isAssigned = assignedOrExternalStats.getKey();
            Map<String, Integer> projectsStats = assignedOrExternalStats.getValue();
            Integer projectsTotalContributions = sumMapValues(projectsStats);

            if (isAssigned) {
                builder.assignedProjectsStats(projectsStats);
                builder.assignedProjectsContributions(projectsTotalContributions);
            } else {
                builder.externalRepositoriesStats(projectsStats);
                builder.externalRepositoriesContributions(projectsTotalContributions);
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
            // merging two builders is not needed
        };
    }

}
