package com.novoda.github.reports.data.mock;

import com.novoda.github.reports.data.RepoDataLayer;
import com.novoda.github.reports.data.model.ProjectRepoStats;

import java.math.BigDecimal;
import java.util.Date;

public class MockRepoDataLayer implements RepoDataLayer {
    @Override
    public ProjectRepoStats getStats(String repo, Date from, Date to) {
        return new ProjectRepoStats(
                repo,
                BigDecimal.valueOf(5),
                BigDecimal.valueOf(4),
                BigDecimal.valueOf(5),
                BigDecimal.valueOf(8),
                BigDecimal.valueOf(23),
                BigDecimal.valueOf(4)
        );
    }
}
