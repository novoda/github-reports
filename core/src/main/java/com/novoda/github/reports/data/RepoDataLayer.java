package com.novoda.github.reports.data;

import com.novoda.github.reports.data.model.ProjectRepoStats;
import com.novoda.github.reports.data.model.Repository;

import java.util.Date;

public interface RepoDataLayer extends DataLayer<Repository> {

    ProjectRepoStats getStats(String repo, Date from, Date to) throws DataLayerException;

}
