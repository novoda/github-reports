package com.novoda.github.reports.data.mock;

import com.novoda.github.reports.data.RepoDataLayer;
import com.novoda.github.reports.data.model.ProjectRepoStats;

import java.util.Date;

public class MockRepoDataLayer implements RepoDataLayer {
    public ProjectRepoStats getStats(String repo, Date from, Date to) {
        return new ProjectRepoStats(repo, 5, 4, 5, 8, 23, 4);
    }
}
