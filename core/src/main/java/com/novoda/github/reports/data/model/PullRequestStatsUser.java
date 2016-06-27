package com.novoda.github.reports.data.model;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class PullRequestStatsUser {

    public static Builder builder() {
        return new AutoValue_PullRequestStatsUser.Builder();
    }

    abstract Long id();

    abstract String username();

    abstract Long mergedPrs();

    abstract Long openedPrs();

    abstract Long otherPeopleCommentsOnUserPrs();

    abstract Long userCommentsOnOtherPeoplePrs();

    abstract Long commentsOnAllPrs();

    abstract Long commentsOnOwnPrs();

    public Long getAverageOtherPeopleCommentsOnUserPrs() {
        return otherPeopleCommentsOnUserPrs() / openedPrs();
    }

    public Long getAverageUserCommentsOnMergedPrs() {
        return userCommentsOnOtherPeoplePrs() / mergedPrs();
    }

    @AutoValue.Builder
    public static abstract class Builder {

        abstract Builder id(Long id);

        abstract Builder username(String username);

        abstract Builder mergedPrs(Long mergedPrs);

        abstract Builder openedPrs(Long openedPrs);

        abstract Builder otherPeopleCommentsOnUserPrs(Long otherPeopleCommentsOnUserPrs);

        abstract Builder userCommentsOnOtherPeoplePrs(Long userCommentsOnOtherPeoplePrs);

        abstract Builder commentsOnAllPrs(Long commentsOnAllPrs);

        abstract Builder commentsOnOwnPrs(Long commentsOnOwnPrs);

        abstract PullRequestStatsUser build();

    }

}
