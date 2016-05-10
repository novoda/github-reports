package com.novoda.core.mock;

import com.novoda.core.data.RepoDataLayer;
import com.novoda.core.stats.ProjectRepoStats;

import java.util.Date;

public class MockRepoDataLayer implements RepoDataLayer {
    @Override
    public ProjectRepoStats getStats(String repo, Date from, Date to) {
        return new ProjectRepoStats(repo, 5, 4, 5, 8, 23, 4);
    }
}
