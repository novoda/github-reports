package com.novoda.github.reports.data.model;

import com.google.auto.value.AutoValue;

import java.math.BigDecimal;

@AutoValue
public abstract class UserContribution implements Stats {

    public static Builder builder() {
        return new AutoValue_UserContribution.Builder();
    }

    abstract String project();

    abstract BigDecimal comments();

    abstract BigDecimal openedPullRequests();

    abstract BigDecimal mergedPullRequests();

    abstract BigDecimal closedPullRequests();

    abstract BigDecimal openedIssues();

    abstract BigDecimal closedIssues();

    @Override
    public String describeStats() {
        return String.format(
                "* \"%s\"\n" +
                        "  + %s comments\n" +
                        "  + %s opened PRs\n" +
                        "  + %s merged PRs\n" +
                        "  + %s closed PRs\n" +
                        "  + %s opened issues\n" +
                        "  + %s closed issues\n",
                project(),
                comments(),
                openedPullRequests(),
                mergedPullRequests(),
                closedPullRequests(),
                openedIssues(),
                closedIssues()
        );
    }

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder project(String project);

        public abstract Builder comments(BigDecimal comments);

        public abstract Builder openedPullRequests(BigDecimal openedPullRequests);

        public abstract Builder mergedPullRequests(BigDecimal mergedPullRequests);

        public abstract Builder closedPullRequests(BigDecimal closedPullRequests);

        public abstract Builder openedIssues(BigDecimal openedIssues);

        public abstract Builder closedIssues(BigDecimal closedIssues);

        public abstract UserContribution build();

    }

}
