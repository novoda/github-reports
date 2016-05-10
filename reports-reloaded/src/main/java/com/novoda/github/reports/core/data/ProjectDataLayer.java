package com.novoda.github.reports.core.data;

import com.novoda.github.reports.core.stats.ProjectRepoStats;

import java.util.Date;

public interface ProjectDataLayer {
    ProjectRepoStats getStats(String project, Date from, Date to);
}
