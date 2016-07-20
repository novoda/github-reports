package com.novoda.github.reports.data.model;

import com.google.auto.value.AutoValue;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AutoValue
public abstract class UserAssignmentsStats implements Stats {

    public static Builder builder() {
        return new AutoValue_UserAssignmentsStats.Builder();
    }

    abstract Map<String, List<UserAssignmentsContributions>> userAssignmentsContributions();

    @Override
    public String describeStats() {
        Map<String, List<UserAssignmentsContributions>> assignments = userAssignmentsContributions();
        return assignments.keySet().stream()
                .map(username -> username + " was assigned to:\n" +
                        assignments.get(username).stream()
                                .map(UserAssignmentsContributions::describeStats)
                                .collect(Collectors.joining("\n")))
                .collect(Collectors.joining("\n\n"));
    }

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder userAssignmentsContributions(
                Map<String, List<UserAssignmentsContributions>> userAssignmentsContributions);

        public abstract UserAssignmentsStats build();

    }

}
