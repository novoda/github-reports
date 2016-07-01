package com.novoda.github.reports.data.model;

import com.google.auto.value.AutoValue;

import java.util.List;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;

@AutoValue
public abstract class PullRequestStatsGroup implements Stats {

    public static Builder builder() {
        return new AutoValue_PullRequestStatsGroup.Builder();
    }

    abstract String name();

    abstract List<PullRequestStatsUser> users();

    @Nullable
    abstract PullRequestStatsUser externalAverage();

    @Nullable
    abstract PullRequestStatsUser teamAverage();

    @Nullable
    abstract PullRequestStatsUser assignedAverage();

    @Nullable
    abstract PullRequestStatsUser filterAverage();

    @AutoValue.Builder
    public static abstract class Builder {

        public abstract Builder name(String name);

        public abstract Builder users(List<PullRequestStatsUser> users);

        public abstract Builder externalAverage(@Nullable PullRequestStatsUser externalAverage);

        public abstract Builder teamAverage(@Nullable PullRequestStatsUser teamAverage);

        public abstract Builder assignedAverage(@Nullable PullRequestStatsUser assignedAverage);

        public abstract Builder filterAverage(@Nullable PullRequestStatsUser filterAverage);

        public abstract PullRequestStatsGroup build();

    }

    @Override
    public String describeStats() {

        String groupName = String.format("GROUP %s\n", name());
        String averages = String.format(
                "\n" +
                        "AVERAGE EXTERNAL: \n%s\n" +
                        "AVERAGE TEAM: \n%s\n" +
                        "AVERAGE ASSIGNED: \n%s\n" +
                        "AVERAGE FILTER: \n%s\n",
                externalAverage(),
                teamAverage(),
                assignedAverage(),
                filterAverage()
        );

        return users().stream()
                .map(PullRequestStatsUser::toString)
                .collect(Collectors.joining("\n", groupName, averages));
    }

}
