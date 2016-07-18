package com.novoda.github.reports.data.model;

import com.google.auto.value.AutoValue;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@AutoValue
public abstract class UserAssignmentsContributions implements UserAssignmentsBase, Stats {

    public static Builder builder() {
        return new AutoValue_UserAssignmentsContributions.Builder()
                .contributions(Collections.emptyList());
    }

    abstract List<UserContribution> contributions();

    @Override
    public String describeStats() {
        return String.format(
                "- \"%s\" (%s to %s), and worked on\n%s",
                assignedRepositories().stream().collect(Collectors.joining(", ")),
                dateToStringOrUnknown(assignmentStart()),
                dateToStringOrUnknown(assignmentEnd()),
                contributions().stream()
                        .map(UserContribution::describeStats)
                        .flatMap(s -> Arrays.stream(s.split("\\n")))
                        .map(s -> "  " + s)
                        .collect(Collectors.joining("\n"))
        );
    }

    private String dateToStringOrUnknown(Date date) {
        if (date == null) {
            return "?";
        }
        return date.toString();
    }

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder assignmentStart(@Nullable Date assignmentStart);

        public abstract Builder assignmentEnd(@Nullable Date assignmentEnd);

        public abstract Builder assignedRepositories(List<String> assignedRepositories);

        public abstract Builder contributions(List<UserContribution> contributions);

        public abstract UserAssignmentsContributions build();

    }
}
