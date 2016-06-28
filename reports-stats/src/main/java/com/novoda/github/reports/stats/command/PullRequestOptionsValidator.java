package com.novoda.github.reports.stats.command;

public class PullRequestOptionsValidator {

    public boolean validate(PullRequestOptions options) {
        return !(options.hasProjects() && options.hasRepositories());
    }

}
