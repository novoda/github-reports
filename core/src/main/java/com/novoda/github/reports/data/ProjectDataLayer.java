package com.novoda.github.reports.data;

import com.novoda.github.reports.data.model.ProjectRepoStats;

import java.util.Date;

public interface ProjectDataLayer {
    ProjectRepoStats getStats(String project, Date from, Date to);
}
