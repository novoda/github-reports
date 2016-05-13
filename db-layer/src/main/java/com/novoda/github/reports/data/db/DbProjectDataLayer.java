package com.novoda.github.reports.data.db;

import com.novoda.github.reports.data.ProjectDataLayer;
import com.novoda.github.reports.data.model.ProjectRepoStats;

import java.util.Date;

public class DbProjectDataLayer implements ProjectDataLayer {
    @Override
    public ProjectRepoStats getStats(String project, Date from, Date to) {
        return null;
    }
}
