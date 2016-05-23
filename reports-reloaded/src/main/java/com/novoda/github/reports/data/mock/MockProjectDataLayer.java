package com.novoda.github.reports.data.mock;

import com.novoda.github.reports.data.ProjectDataLayer;
import com.novoda.github.reports.data.model.ProjectRepoStats;

import java.math.BigInteger;
import java.util.Date;

public class MockProjectDataLayer implements ProjectDataLayer {
    @Override
    public ProjectRepoStats getStats(String project, Date from, Date to) {
        return new ProjectRepoStats(
                project,
                BigInteger.valueOf(12),
                BigInteger.valueOf(13),
                BigInteger.valueOf(48),
                BigInteger.valueOf(96),
                BigInteger.valueOf(123),
                BigInteger.valueOf(8)
        );
    }
}
