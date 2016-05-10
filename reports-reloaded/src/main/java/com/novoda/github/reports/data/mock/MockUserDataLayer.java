package com.novoda.github.reports.data.mock;

import com.novoda.github.reports.data.UserDataLayer;
import com.novoda.github.reports.data.model.UserStats;

import java.util.Date;

public class MockUserDataLayer implements UserDataLayer {
    public UserStats getStats(String user, String repo, Date from, Date to) {
        return new UserStats(user, 25, 123, 896, 65, 437, 7);
    }
}
