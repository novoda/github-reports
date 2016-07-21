package com.novoda.github.reports.data.model;

import com.google.auto.value.AutoValue;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@AutoValue
public abstract class AggregatedUserStats implements Stats {

    public static Builder builder() {
        return new AutoValue_AggregatedUserStats.Builder();
    }

    abstract Map<String, Integer> assignedProjectsStats();

    abstract Map<String, Integer> externalProjectsStats();

    abstract Integer assignedProjectsContributions();

    abstract Integer externalProjectsContributions();

    @Override
    public String describeStats() {
        return "* assigned projects\n" +
                describeMapWithTotal(assignedProjectsStats(), assignedProjectsContributions()) +
                "* external projects\n" +
                describeMapWithTotal(externalProjectsStats(), externalProjectsContributions());
    }

    private String describeMapWithTotal(Map<String, Integer> contributionsMap, Integer contributionsTotal) {
        return contributionsMap.entrySet()
                .stream()
                .map(projectContributionToString())
                .collect(Collectors.joining("\n", "", getTotalContributionsSuffix(contributionsTotal)));
    }

    private Function<Map.Entry<String, Integer>, String> projectContributionToString() {
        return projectContribution -> String.format("  + %s %d", projectContribution.getKey(), projectContribution.getValue());
    }

    private String getTotalContributionsSuffix(Integer contributionsTotal) {
        return String.format("\n  TOTAL: %d\n", contributionsTotal);
    }

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder assignedProjectsStats(Map<String, Integer> assignedProjectsStats);

        public abstract Builder externalProjectsStats(Map<String, Integer> externalProjectsStats);

        public abstract Builder assignedProjectsContributions(Integer assignedProjectsContributions);

        public abstract Builder externalProjectsContributions(Integer externalProjectsContributions);

        public abstract AggregatedUserStats build();

    }

}
