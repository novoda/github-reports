package com.novoda.core.mock;

import com.novoda.core.data.ProjectDataLayer;
import com.novoda.core.stats.ProjectRepoStats;

import java.util.Date;

public class MockProjectDataLayer implements ProjectDataLayer {
    @Override
    public ProjectRepoStats getStats(String project, Date from, Date to) {
        return new ProjectRepoStats(project, 12, 13, 48, 96, 123, 8);
    }
}
