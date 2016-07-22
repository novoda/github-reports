package com.novoda.github.reports.stats.handler;

import com.novoda.floatschedule.convert.FloatGithubProjectConverter;
import com.novoda.github.reports.data.DataLayerException;
import com.novoda.github.reports.data.RepoDataLayer;
import com.novoda.github.reports.data.model.ProjectRepoStats;
import com.novoda.github.reports.stats.command.ProjectOptions;

import java.io.IOException;
import java.util.List;

public class ProjectCommandHandler implements CommandHandler<ProjectRepoStats, ProjectOptions> {
    private final RepoDataLayer dataLayer;
    private final FloatGithubProjectConverter floatGithubProjectConverter;

    public ProjectCommandHandler(RepoDataLayer dataLayer, FloatGithubProjectConverter floatGithubProjectConverter) {
        this.dataLayer = dataLayer;
        this.floatGithubProjectConverter = floatGithubProjectConverter;
    }

    @Override
    public ProjectRepoStats handle(ProjectOptions options) {
        try {
            String project = options.getProject();
            List<String> repositories = floatGithubProjectConverter.getRepositories(project);
            ProjectRepoStats stats = dataLayer.getStats(repositories, options.getFrom(), options.getTo());
            return new ProjectRepoStats(project, stats.getEventStats(), stats.getNumberOfParticipatingUsers());
        } catch (DataLayerException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
