package com.novoda.core.data;

import com.novoda.core.stats.ProjectRepoStats;

import java.util.Date;

public interface RepoDataLayer {
    ProjectRepoStats getStats(String repo, Date from, Date to);
}
