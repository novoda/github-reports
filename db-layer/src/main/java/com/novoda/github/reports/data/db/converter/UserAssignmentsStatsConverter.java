package com.novoda.github.reports.data.db.converter;

import com.novoda.github.reports.data.model.UserAssignmentsContributions;
import com.novoda.github.reports.data.model.UserAssignmentsStats;
import com.novoda.github.reports.data.model.UserContribution;
import org.jetbrains.annotations.NotNull;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static com.novoda.github.reports.data.db.DatabaseHelper.*;
import static com.novoda.github.reports.data.db.Tables.EVENT;
import static com.novoda.github.reports.data.db.builder.EventUserAssignmentsQueryBuilder.*;

public class UserAssignmentsStatsConverter {

    private static final Field[] singleUserGroupingFields = new Field[]{
            DATE_FROM_FIELD,
            DATE_TO_FIELD,
            REPOSITORIES_ASSIGNED_FIELD
    };

    public UserAssignmentsStats convert(Map<String, ? extends Result<? extends Record>> resultsGroupedByUsername) {
        Map<String, List<UserAssignmentsContributions>> userAssignmentsContributions = resultsGroupedByUsername
                .entrySet()
                .stream()
                .map(valuesToSingleUserAssignmentsContributions())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return UserAssignmentsStats
                .builder()
                .userAssignmentsContributions(userAssignmentsContributions)
                .build();
    }

    @NotNull
    private Function<Map.Entry<String, ? extends Result<? extends Record>>, ImmutableMapEntry<String, List<UserAssignmentsContributions>>> valuesToSingleUserAssignmentsContributions() {
        return singleUserStats -> new ImmutableMapEntry<String, List<UserAssignmentsContributions>>() {
            @Override
            public String getKey() {
                return singleUserStats.getKey();
            }

            @Override
            public List<UserAssignmentsContributions> getValue() {
                return singleUserStats.getValue()
                        .intoGroups(singleUserGroupingFields)
                        .entrySet()
                        .stream()
                        .map(toSingleUserAssignmentsContributions())
                        .collect(Collectors.toList());
            }
        };
    }

    @NotNull
    private Function<Map.Entry<Record, ? extends Result<? extends Record>>, UserAssignmentsContributions> toSingleUserAssignmentsContributions() {
        return singleUserMultipleRepositoriesStats -> {
            Result<? extends Record> multipleRepositoriesStats = singleUserMultipleRepositoriesStats.getValue();
            List<UserContribution> contributions = aggregateIntoUserContributions(multipleRepositoriesStats);

            Record groupingKeys = singleUserMultipleRepositoriesStats.getKey();
            UserAssignmentsContributions.Builder builder = UserAssignmentsContributions.builder()
                    .assignmentStart(groupingKeys.getValue(DATE_FROM_FIELD))
                    .assignmentEnd(groupingKeys.getValue(DATE_TO_FIELD))
                    .assignedRepositories(splitAssignedRepositories(groupingKeys))
                    .contributions(contributions);

            return builder.build();
        };
    }

    @NotNull
    private List<UserContribution> aggregateIntoUserContributions(Result<? extends Record> multipleRepositoriesStats) {
        return multipleRepositoriesStats
                .stream()
                .collect(groupByRepositoryWorkedOn())
                .entrySet()
                .stream()
                .map(toUserContributionBuilder())
                .map(UserContribution.Builder::build)
                .collect(Collectors.toList());
    }

    @NotNull
    private Collector<Record, ?, Map<String, List<Record>>> groupByRepositoryWorkedOn() {
        return Collectors.groupingBy(
                record -> record.getValue(REPOSITORY_WORKED_NAME_FIELD)
        );
    }

    @NotNull
    private Function<Map.Entry<String, List<Record>>, UserContribution.Builder> toUserContributionBuilder() {
        return singleUserSingleRepositoryStats ->
                singleUserSingleRepositoryStats.getValue()
                        .stream()
                        .collect(
                                supplyBuilder(singleUserSingleRepositoryStats),
                                accumulateRecordsIntoBuilder(),
                                doNotCombine()
                        );
    }

    @NotNull
    private Supplier<UserContribution.Builder> supplyBuilder(Map.Entry<String, List<Record>> singleUserSingleRepositoryStats) {
        return () -> UserContribution.builder().project(singleUserSingleRepositoryStats.getKey());
    }

    @NotNull
    private BiConsumer<UserContribution.Builder, Record> accumulateRecordsIntoBuilder() {
        return (builder, record) -> {
            Integer eventTypeId = record.getValue(EVENT.EVENT_TYPE_ID);
            Integer value = record.getValue(COUNT_EVENT_FIELD);
            if (eventTypeId.equals(COMMENTED_ISSUES_ID) || eventTypeId.equals(COMMENTED_PULL_REQUESTS_ID)) {
                builder.plusComments(value);
            } else if (eventTypeId.equals(OPENED_PULL_REQUESTS_ID)) {
                builder.openedPullRequests(value);
            } else if (eventTypeId.equals(MERGED_PULL_REQUESTS_ID)) {
                builder.mergedPullRequests(value);
            } else if (eventTypeId.equals(CLOSED_PULL_REQUESTS_ID)) {
                builder.closedPullRequests(value);
            } else if (eventTypeId.equals(OPENED_ISSUES_ID)) {
                builder.openedIssues(value);
            } else if (eventTypeId.equals(CLOSED_ISSUES_ID)) {
                builder.openedIssues(value);
            }
        };
    }

    @NotNull
    private BiConsumer<UserContribution.Builder, UserContribution.Builder> doNotCombine() {
        return (builder, builder2) -> {
            throw new IllegalStateException("Combining Builders is not supported.");
        };
    }

    @NotNull
    private List<String> splitAssignedRepositories(Record groupingKeys) {
        return Arrays.asList(groupingKeys.getValue(REPOSITORIES_ASSIGNED_FIELD).split(","));
    }

}
