package com.novoda.github.reports.data.model;

import com.google.auto.value.AutoValue;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@AutoValue
public abstract class AggregatedUserStats implements Stats {

    private static final String NEW_LINE = "\n";

    public static Builder builder() {
        return new AutoValue_AggregatedUserStats.Builder()
                .assignedProjectsContributions(0)
                .assignedProjectsStats(Collections.emptyMap())
                .externalRepositoriesContributions(0)
                .externalRepositoriesStats(Collections.emptyMap());
    }

    abstract Map<String, Integer> assignedProjectsStats();

    abstract Integer assignedProjectsContributions();

    abstract Map<String, Integer> externalRepositoriesStats();

    abstract Integer externalRepositoriesContributions();

    @Override
    public String describeStats() {
        return "* assigned projects" + NEW_LINE +
                describeMapWithTotal(assignedProjectsStats(), assignedProjectsContributions()) +
                "* external repositories" + NEW_LINE +
                describeMapWithTotal(externalRepositoriesStats(), externalRepositoriesContributions());
    }

    private String describeMapWithTotal(Map<String, Integer> contributionsMap, Integer contributionsTotal) {
        return contributionsMap.entrySet()
                .stream()
                .map(contributionToString())
                .collect(Collectors.joining(NEW_LINE, "", getTotalContributionsSuffix(contributionsTotal)));
    }

    private Function<Map.Entry<String, Integer>, String> contributionToString() {
        return contribution -> String.format("  + %s %d", contribution.getKey(), contribution.getValue());
    }

    private String getTotalContributionsSuffix(Integer contributionsTotal) {
        return String.format("%s  TOTAL: %d%s", NEW_LINE, contributionsTotal, NEW_LINE);
    }

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder assignedProjectsStats(Map<String, Integer> assignedProjectsStats);

        public abstract Builder assignedProjectsContributions(Integer assignedProjectsContributions);

        public abstract Builder externalRepositoriesStats(Map<String, Integer> externalRepositoriesStats);

        public abstract Builder externalRepositoriesContributions(Integer externalRepositoriesContributions);

        public abstract AggregatedUserStats build();

    }

}
