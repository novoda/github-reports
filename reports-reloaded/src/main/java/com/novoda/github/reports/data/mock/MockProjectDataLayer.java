package com.novoda.github.reports.data.mock;

import com.novoda.github.reports.data.ProjectDataLayer;
import com.novoda.github.reports.data.model.ProjectRepoStats;

import java.math.BigDecimal;
import java.util.Date;

public class MockProjectDataLayer implements ProjectDataLayer {
    @Override
    public ProjectRepoStats getStats(String project, Date from, Date to) {
        return new ProjectRepoStats(
                project,
                BigDecimal.valueOf(12),
                BigDecimal.valueOf(13),
                BigDecimal.valueOf(48),
                BigDecimal.valueOf(96),
                BigDecimal.valueOf(123),
                BigDecimal.valueOf(8)
        );
    }
}
