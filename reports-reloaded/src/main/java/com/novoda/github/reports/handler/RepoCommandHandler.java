package com.novoda.github.reports.handler;

import com.novoda.github.reports.command.RepoOptions;
import com.novoda.github.reports.core.data.RepoDataLayer;
import com.novoda.github.reports.core.stats.ProjectRepoStats;

public class RepoCommandHandler implements CommandHandler<ProjectRepoStats, RepoOptions> {
    private final RepoDataLayer dataLayer;

    public RepoCommandHandler(RepoDataLayer dataLayer) {
        this.dataLayer = dataLayer;
    }

    @Override
    public ProjectRepoStats handle(RepoOptions options) {
        return dataLayer.getStats(options.getRepo(), options.getFrom(), options.getTo());
    }
}
