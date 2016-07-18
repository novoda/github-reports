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

    abstract Map<String, List<UserAssignment>> userAssignments();

    @Override
    public String describeStats() {
        Map<String, List<UserAssignment>> assignments = userAssignments();
        return assignments.keySet().stream()
                .map(username -> username + " was assigned to:\n" +
                        assignments.get(username).stream()
                                .map(UserAssignment::describeStats)
                                .collect(Collectors.joining("\n")))
                .collect(Collectors.joining("\n\n"));
    }

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder userAssignments(Map<String, List<UserAssignment>> userAssignments);

        public abstract UserAssignmentsStats build();

    }

}
