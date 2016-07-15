package com.novoda.github.reports.data.model;

import com.google.auto.value.AutoValue;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@AutoValue
public abstract class UserAssignment implements Stats {

    public static Builder builder() {
        return new AutoValue_UserAssignment.Builder();
    }

    @Nullable
    abstract Date assignmentStart();

    @Nullable
    abstract Date assignmentEnd();

    abstract String assignedProject();

    abstract List<UserContribution> contributions();

    @Override
    public String describeStats() {
        return String.format(
                "- \"%s\" (%s to %s), and worked on\n%s",
                assignedProject(),
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

        public abstract Builder assignedProject(String assignedProject);

        public abstract Builder contributions(List<UserContribution> contributions);

        public abstract UserAssignment build();

    }
}
