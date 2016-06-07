package com.novoda.github.reports.data;

import com.novoda.github.reports.data.model.DatabaseRepository;
import com.novoda.github.reports.data.model.ProjectRepoStats;

import java.util.Date;

public interface RepoDataLayer extends DataLayer<DatabaseRepository> {

    ProjectRepoStats getStats(String repo, Date from, Date to) throws DataLayerException;

}
