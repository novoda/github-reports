package com.novoda.github.reports.data.model;

import com.google.auto.value.AutoValue;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@AutoValue
public abstract class UserAssignments implements UserAssignmentsBase, Stats {

    public static Builder builder() {
        return new AutoValue_UserAssignments.Builder();
    }

    @Override
    public String describeStats() {
        return String.format(
                "- \"%s\" (%s to %s)\n",
                assignedRepositories().stream().collect(Collectors.joining(", ")),
                dateToStringOrUnknown(assignmentStart()),
                dateToStringOrUnknown(assignmentEnd())
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

        public abstract UserAssignments build();

    }
}
