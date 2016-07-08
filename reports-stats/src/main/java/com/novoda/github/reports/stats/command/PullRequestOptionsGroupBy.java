package com.novoda.github.reports.stats.command;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum PullRequestOptionsGroupBy {

    NONE(null, null),
    WEEK("week", "w"),
    MONTH("month", "m");

    private final Set<String> groupDescriptors;

    PullRequestOptionsGroupBy(String... groupDescriptors) {
        List<String> strings = Arrays.asList(groupDescriptors);
        this.groupDescriptors = new HashSet<>(strings);
    }

    public Set<String> getGroupDescriptors() {
        return groupDescriptors;
    }

}
