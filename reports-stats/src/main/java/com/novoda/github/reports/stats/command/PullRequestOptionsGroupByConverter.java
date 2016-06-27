package com.novoda.github.reports.stats.command;

import com.beust.jcommander.IStringConverter;

import java.util.Set;

import static com.novoda.github.reports.stats.command.PullRequestOptionsGroupBy.NONE;

class PullRequestOptionsGroupByConverter implements IStringConverter<PullRequestOptionsGroupBy> {

    @Override
    public PullRequestOptionsGroupBy convert(String value) {
        for (PullRequestOptionsGroupBy pullRequestGroup : PullRequestOptionsGroupBy.values()) {
            Set<String> groupDescriptors = pullRequestGroup.getGroupDescriptors();
            if (groupDescriptors.contains(value)) {
                return pullRequestGroup;
            }
        }
        return NONE;
    }

}
