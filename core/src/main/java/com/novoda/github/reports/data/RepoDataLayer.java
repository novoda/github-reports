package com.novoda.github.reports.data;

import com.novoda.github.reports.data.model.ProjectRepoStats;
import com.novoda.github.reports.data.model.Repository;

import java.util.Date;
import java.util.List;

public interface RepoDataLayer extends DataLayer<Repository> {

    ProjectRepoStats getStats(List<String> repositories, Date from, Date to) throws DataLayerException;

    ProjectRepoStats getStats(String repo, Date from, Date to) throws DataLayerException;

    List<Repository> getRepositories() throws DataLayerException;

}
