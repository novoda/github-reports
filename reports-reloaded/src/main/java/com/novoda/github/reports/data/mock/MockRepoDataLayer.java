package com.novoda.github.reports.data.mock;

import com.novoda.github.reports.data.RepoDataLayer;
import com.novoda.github.reports.data.model.EventStats;
import com.novoda.github.reports.data.model.ProjectRepoStats;
import com.novoda.github.reports.data.model.Repository;

import java.math.BigInteger;
import java.util.Date;

public class MockRepoDataLayer implements RepoDataLayer {
    @Override
    public Repository updateOrInsert(Repository repository) {
        return repository;
    }

    @Override
    public ProjectRepoStats getStats(String repo, Date from, Date to) {
        return new ProjectRepoStats(
                repo,
                new EventStats(
                        BigInteger.valueOf(5),
                        BigInteger.valueOf(4),
                        BigInteger.valueOf(5),
                        BigInteger.valueOf(8),
                        BigInteger.valueOf(23)
                ),
                BigInteger.valueOf(4)
        );
    }
}
