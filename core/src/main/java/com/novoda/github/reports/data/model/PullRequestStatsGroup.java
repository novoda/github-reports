package com.novoda.github.reports.data.model;

import com.google.auto.value.AutoValue;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

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
    abstract PullRequestStatsUser organisationAverage();

    @Nullable
    abstract PullRequestStatsUser assignedAverage();

    @AutoValue.Builder
    public static abstract class Builder {

        public abstract Builder name(String name);

        public abstract Builder users(List<PullRequestStatsUser> users);

        public abstract Builder externalAverage(@Nullable PullRequestStatsUser externalAverage);

        public abstract Builder organisationAverage(@Nullable PullRequestStatsUser organisationAverage);

        public abstract Builder assignedAverage(@Nullable PullRequestStatsUser assignedAverage);

        public abstract PullRequestStatsGroup build();

    }

    @Override
    public String describeStats() {

        String groupName = String.format("GROUP %s\n", name());
        String averages = String.format(
                "\n" +
                        "AVERAGE EXTERNAL: \n%s\n" +
                        "AVERAGE ORGANISATION: \n%s\n" +
                        "AVERAGE ASSIGNED: \n%s\n",
                externalAverage(),
                organisationAverage(),
                assignedAverage()
        );

        return users().stream()
                .map(PullRequestStatsUser::toString)
                .collect(Collectors.joining("\n", groupName, averages));
    }

}
