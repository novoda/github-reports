package com.novoda.github.reports.data.model;

import com.google.auto.value.AutoValue;

import java.util.List;

@AutoValue
public abstract class PullRequestStats implements Stats {

    abstract List<PullRequestStatsGroup> groups();

    public static Builder builder() {
        return new AutoValue_PullRequestStats.Builder();
    }

    @Override
    public String describeStats() {
        // TODO describe statistics
        return null;
    }

    @AutoValue.Builder
    public static abstract class Builder {
        abstract Builder groups(List<PullRequestStatsGroup> groups);

        abstract PullRequestStats build();
    }

}
