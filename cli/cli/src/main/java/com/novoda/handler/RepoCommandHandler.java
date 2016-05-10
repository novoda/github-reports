package com.novoda.handler;

import com.novoda.command.RepoOptions;
import com.novoda.core.data.RepoDataLayer;
import com.novoda.core.stats.ProjectRepoStats;

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
