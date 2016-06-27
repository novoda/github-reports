package com.novoda.github.reports.stats.command;

public class PullRequestOptionsValidator {

    public void validate(PullRequestOptions options) throws OptionsNotValidException {

        if (options.hasProjects() && options.hasRepositories()) {
            throw new OptionsNotValidException("You can't specify both projects and repositories in the options.");
        }

    }

}
