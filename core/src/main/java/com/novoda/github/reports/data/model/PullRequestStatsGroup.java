package com.novoda.github.reports.data.model;

import com.google.auto.value.AutoValue;

import java.util.Date;
import java.util.List;

@AutoValue
public abstract class PullRequestStatsGroup {

    public static Builder builder() {
        return new AutoValue_PullRequestStatsGroup.Builder();
    }

    abstract Date from();

    abstract Date to();

    abstract String name();

    abstract List<PullRequestStatsUser> users();

    abstract PullRequestStatsUser externalAverage();

    abstract PullRequestStatsUser teamAverage();

    abstract PullRequestStatsUser projectAverage();

    abstract PullRequestStatsUser filterAverage();

    @AutoValue.Builder
    public static abstract class Builder {
        abstract Builder from(Date from);

        abstract Builder to(Date to);

        abstract Builder name(String name);

        abstract Builder users(List<PullRequestStatsUser> users);

        abstract Builder externalAverage(PullRequestStatsUser externalAverage);

        abstract Builder teamAverage(PullRequestStatsUser teamAverage);

        abstract Builder projectAverage(PullRequestStatsUser projectAverage);

        abstract Builder filterAverage(PullRequestStatsUser filterAverage);

        abstract PullRequestStatsGroup build();
    }

}
