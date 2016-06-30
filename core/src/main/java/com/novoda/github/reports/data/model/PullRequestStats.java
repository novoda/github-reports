package com.novoda.github.reports.data.model;

import com.google.auto.value.AutoValue;

import java.util.List;
import java.util.stream.Collectors;

@AutoValue
public abstract class PullRequestStats implements Stats {

    abstract List<PullRequestStatsGroup> groups();

    public static Builder builder() {
        return new AutoValue_PullRequestStats.Builder();
    }

    @AutoValue.Builder
    public static abstract class Builder {
        public abstract Builder groups(List<PullRequestStatsGroup> groups);

        public abstract PullRequestStats build();
    }

    @Override
    public String describeStats() {
        return groups().stream()
                .map(PullRequestStatsGroup::describeStats)
                .collect(Collectors.joining("\n"));
    }

}
