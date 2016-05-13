package com.novoda.github.reports.data.mock;

import com.novoda.github.reports.data.ProjectDataLayer;
import com.novoda.github.reports.data.model.ProjectRepoStats;

import java.util.Date;

public class MockProjectDataLayer implements ProjectDataLayer {
    @Override
    public ProjectRepoStats getStats(String project, Date from, Date to) {
        return new ProjectRepoStats(project, 12, 13, 48, 96, 123, 8);
    }
}
