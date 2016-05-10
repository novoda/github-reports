package com.novoda.core.data;

import com.novoda.core.stats.ProjectRepoStats;

import java.util.Date;

public interface ProjectDataLayer {
    ProjectRepoStats getStats(String project, Date from, Date to);
}
