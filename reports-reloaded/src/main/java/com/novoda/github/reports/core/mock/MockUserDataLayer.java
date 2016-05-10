package com.novoda.github.reports.core.mock;

import com.novoda.github.reports.core.data.UserDataLayer;
import com.novoda.github.reports.core.stats.UserStats;

import java.util.Date;

public class MockUserDataLayer implements UserDataLayer {
    @Override
    public UserStats getStats(String user, String repo, Date from, Date to) {
        return new UserStats(user, 25, 123, 896, 65, 437, 7);
    }
}
