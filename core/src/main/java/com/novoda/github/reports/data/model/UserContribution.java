package com.novoda.github.reports.data.model;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class UserContribution implements Stats {

    private static final Integer ZERO = 0;

    public static Builder builder() {
        return new AutoValue_UserContribution.Builder()
                .comments(ZERO)
                .openedPullRequests(ZERO)
                .mergedPullRequests(ZERO)
                .closedPullRequests(ZERO)
                .openedIssues(ZERO)
                .closedIssues(ZERO);
    }

    abstract String project();

    abstract Integer comments();

    abstract Integer openedPullRequests();

    abstract Integer mergedPullRequests();

    abstract Integer closedPullRequests();

    abstract Integer openedIssues();

    abstract Integer closedIssues();

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

        public abstract Builder comments(Integer comments);

        public abstract Builder openedPullRequests(Integer openedPullRequests);

        public abstract Builder mergedPullRequests(Integer mergedPullRequests);

        public abstract Builder closedPullRequests(Integer closedPullRequests);

        public abstract Builder openedIssues(Integer openedIssues);

        public abstract Builder closedIssues(Integer closedIssues);

        public Builder plusComments(Integer comments) {
            return this.comments(build().comments() + comments);
        }

        public abstract UserContribution build();

    }

}
