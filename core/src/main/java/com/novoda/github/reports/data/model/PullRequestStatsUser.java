package com.novoda.github.reports.data.model;

import com.google.auto.value.AutoValue;

import java.math.BigDecimal;

@AutoValue
public abstract class PullRequestStatsUser {

    public static Builder builder() {
        return new AutoValue_PullRequestStatsUser.Builder();
    }

    public abstract Long id();

    abstract String username();

    abstract BigDecimal mergedPrs();

    abstract BigDecimal openedPrs();

    abstract BigDecimal otherPeopleCommentsOnUserPrs();

    abstract BigDecimal userCommentsOnOtherPeoplePrs();

    abstract BigDecimal commentsOnAllPrs();

    abstract BigDecimal commentsOnOwnPrs();

    abstract BigDecimal averageOtherPeopleCommentsOnUserPrs();

    abstract BigDecimal averageUserCommentsOnMergedPrs();

    abstract UserType type();

    public abstract String toString();

    @AutoValue.Builder
    public static abstract class Builder {

        public abstract Builder id(Long id);

        public abstract Builder username(String username);

        public abstract Builder mergedPrs(BigDecimal mergedPrs);

        public abstract Builder openedPrs(BigDecimal openedPrs);

        public abstract Builder otherPeopleCommentsOnUserPrs(BigDecimal otherPeopleCommentsOnUserPrs);

        public abstract Builder userCommentsOnOtherPeoplePrs(BigDecimal userCommentsOnOtherPeoplePrs);

        public abstract Builder commentsOnAllPrs(BigDecimal commentsOnAllPrs);

        public abstract Builder commentsOnOwnPrs(BigDecimal commentsOnOwnPrs);

        public abstract Builder averageOtherPeopleCommentsOnUserPrs(BigDecimal averageOtherPeopleCommentsOnUserPrs);

        public abstract Builder averageUserCommentsOnMergedPrs(BigDecimal averageUserCommentsOnMergedPrs);

        public abstract Builder type(UserType type);

        public abstract PullRequestStatsUser build();

    }

    public enum UserType {
        EXTERNAL,
        TEAM,
        ASSIGNED,
        FILTER
    }

}
