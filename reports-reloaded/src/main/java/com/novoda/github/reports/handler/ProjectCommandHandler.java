package com.novoda.github.reports.handler;

import com.novoda.github.reports.command.ProjectOptions;
import com.novoda.github.reports.data.DataLayerException;
import com.novoda.github.reports.data.ProjectDataLayer;
import com.novoda.github.reports.data.model.ProjectRepoStats;

public class ProjectCommandHandler implements CommandHandler<ProjectRepoStats, ProjectOptions> {
    private final ProjectDataLayer dataLayer;

    public ProjectCommandHandler(ProjectDataLayer dataLayer) {
        this.dataLayer = dataLayer;
    }

    @Override
    public ProjectRepoStats handle(ProjectOptions options) {
        try {
            return dataLayer.getStats(options.getProject(), options.getFrom(), options.getTo());
        } catch (DataLayerException e) {
            e.printStackTrace();
        }
        return null;
    }
}
