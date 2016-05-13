package com.novoda.github.reports.data.db;

import com.novoda.github.reports.data.RepoDataLayer;
import com.novoda.github.reports.data.model.ProjectRepoStats;

import java.util.Date;

public class DbRepoDataLayer implements RepoDataLayer {
    @Override
    public ProjectRepoStats getStats(String repo, Date from, Date to) {
        return null;
    }
}
