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

import static com.novoda.github.reports.data.db.builder.EventUserAggregatedWorkQueryBuilder.*;

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
                .map(userGroupToAggregatedUserStats())
                .collect(toMap());
    }

    private Function<Map.Entry<String, ? extends Result<? extends Record>>, SimpleImmutableEntry<String, AggregatedUserStats>> userGroupToAggregatedUserStats() {
        return userStats -> {
            String username = userStats.getKey();
            Result<? extends Record> allUserRecords = userStats.getValue();
            AggregatedUserStats aggregatedUserStats = buildAggregatedUserStatsFromAllUserRecords(allUserRecords);

            return new SimpleImmutableEntry<>(username, aggregatedUserStats);
        };
    }

    private AggregatedUserStats buildAggregatedUserStatsFromAllUserRecords(Result<? extends Record> userStats) {
        return userStats
                .intoGroups(WAS_SCHEDULED_WORK_FIELD)
                .entrySet()
                .stream()
                .map(assignedOrExternalRecordGroupsToMapEntries())
                .collect(
                        AggregatedUserStats::builder,
                        mapEntriesIntoAggregatedUserStats(),
                        noopMerger()
                )
                .build();
    }

    private Function<Map.Entry<Boolean, ? extends Result<? extends Record>>, SimpleImmutableEntry<Boolean, Map<String, Integer>>> assignedOrExternalRecordGroupsToMapEntries() {
        return assignedOrExternalRecordGroup -> {
            Boolean isAssigned = assignedOrExternalRecordGroup.getKey();
            Field<String> groupKey = getGroupingProjectOrRepositoryKey(isAssigned);

            Map<String, Integer> projectOrRepositorySetStats = buildProjectOrRepositorySetStatsFromRecords(
                    groupKey,
                    assignedOrExternalRecordGroup.getValue()
            );

            return new SimpleImmutableEntry<>(isAssigned, projectOrRepositorySetStats);
        };
    }

    private Field<String> getGroupingProjectOrRepositoryKey(boolean isAssigned) {
        return isAssigned ? PROJECT_ASSIGNED_FIELD : REPOSITORY_WORKED_NAME_FIELD;
    }

    private Map<String, Integer> buildProjectOrRepositorySetStatsFromRecords(Field<String> groupKey, Result<? extends Record> records) {
        return records
                        .intoGroups(groupKey)
                        .entrySet()
                        .stream()
                        .map(projectOrRepositoryRecordsToCount())
                        .collect(toMap());
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
            Map<String, Integer> stats = assignedOrExternalStats.getValue();
            Integer totalContributions = sumMapValues(stats);

            setStatsAndContributions(builder, isAssigned, stats, totalContributions);
        };
    }

    private Integer sumMapValues(Map<String, Integer> projectsStats) {
        return projectsStats
                .entrySet()
                .stream()
                .collect(Collectors.summingInt(Map.Entry::getValue));
    }

    private void setStatsAndContributions(AggregatedUserStats.Builder builder,
                                          boolean isAssigned,
                                          Map<String, Integer> stats,
                                          Integer totalContributions) {

        if (isAssigned) {
            builder.assignedProjectsStats(stats);
            builder.assignedProjectsContributions(totalContributions);
        } else {
            builder.externalRepositoriesStats(stats);
            builder.externalRepositoriesContributions(totalContributions);
        }
    }

    private BiConsumer<AggregatedUserStats.Builder, AggregatedUserStats.Builder> noopMerger() {
        return (fromBuilder, toBuilder) -> {
            // merging two builders is not needed
        };
    }

}
