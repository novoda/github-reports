package com.novoda.github.reports.stats.handler;

import com.novoda.floatschedule.convert.FailedToLoadMappingsException;
import com.novoda.floatschedule.convert.GithubProjectConverter;
import com.novoda.github.reports.data.DataLayerException;
import com.novoda.github.reports.data.RepoDataLayer;
import com.novoda.github.reports.data.model.ProjectRepoStats;
import com.novoda.github.reports.stats.command.ProjectOptions;

import java.util.List;

public class ProjectCommandHandler implements CommandHandler<ProjectRepoStats, ProjectOptions> {
    private final RepoDataLayer dataLayer;
    private final GithubProjectConverter floatGithubProjectConverter;

    public ProjectCommandHandler(RepoDataLayer dataLayer, GithubProjectConverter floatGithubProjectConverter) {
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
        } catch (DataLayerException | FailedToLoadMappingsException e) {
            e.printStackTrace();
        }
        return null;
    }
}
