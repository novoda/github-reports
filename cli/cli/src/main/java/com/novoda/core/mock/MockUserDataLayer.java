package com.novoda.core.mock;

import com.novoda.core.data.UserDataLayer;
import com.novoda.core.stats.UserStats;

import java.util.Date;

public class MockUserDataLayer implements UserDataLayer {
    @Override
    public UserStats getStats(String user, String repo, Date from, Date to) {
        return new UserStats(user, 25, 123, 896, 65, 437, 7);
    }
}
