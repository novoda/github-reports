package com.novoda.github.reports.data;

import com.novoda.github.reports.data.model.ProjectRepoStats;

import java.util.Date;

@FunctionalInterface
public interface RepoDataLayer {

    ProjectRepoStats getStats(String repo, Date from, Date to) throws DataLayerException;

}
