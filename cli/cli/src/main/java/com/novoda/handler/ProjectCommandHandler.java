package com.novoda.handler;

import com.novoda.command.ProjectOptions;
import com.novoda.core.data.ProjectDataLayer;
import com.novoda.core.stats.ProjectRepoStats;

public class ProjectCommandHandler implements CommandHandler<ProjectRepoStats, ProjectOptions> {
    private final ProjectDataLayer dataLayer;

    public ProjectCommandHandler(ProjectDataLayer dataLayer) {
        this.dataLayer = dataLayer;
    }

    @Override
    public ProjectRepoStats handle(ProjectOptions options) {
        return dataLayer.getStats(options.getProject(), options.getFrom(), options.getTo());
    }
}
