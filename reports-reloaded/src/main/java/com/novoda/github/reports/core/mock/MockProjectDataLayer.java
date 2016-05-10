package com.novoda.github.reports.core.mock;

import com.novoda.github.reports.core.data.ProjectDataLayer;
import com.novoda.github.reports.core.stats.ProjectRepoStats;

import java.util.Date;

public class MockProjectDataLayer implements ProjectDataLayer {
    @Override
    public ProjectRepoStats getStats(String project, Date from, Date to) {
        return new ProjectRepoStats(project, 12, 13, 48, 96, 123, 8);
    }
}
