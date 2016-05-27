package com.novoda.github.reports.stats.handler;

import com.novoda.github.reports.stats.command.RepoOptions;
import com.novoda.github.reports.data.DataLayerException;
import com.novoda.github.reports.data.RepoDataLayer;
import com.novoda.github.reports.data.model.ProjectRepoStats;

public class RepoCommandHandler implements CommandHandler<ProjectRepoStats, RepoOptions> {
    private final RepoDataLayer dataLayer;

    public RepoCommandHandler(RepoDataLayer dataLayer) {
        this.dataLayer = dataLayer;
    }

    @Override
    public ProjectRepoStats handle(RepoOptions options) {
        try {
            return dataLayer.getStats(options.getRepo(), options.getFrom(), options.getTo());
        } catch (DataLayerException e) {
            e.printStackTrace();
        }
        return null;
    }
}
