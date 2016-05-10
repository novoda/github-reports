package com.novoda.github.reports.core.data;

import com.novoda.github.reports.core.stats.ProjectRepoStats;

import java.util.Date;

public interface RepoDataLayer {
    ProjectRepoStats getStats(String repo, Date from, Date to);
}
