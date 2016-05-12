package com.novoda.github.reports.data.db;

import com.novoda.github.reports.data.UserDataLayer;
import com.novoda.github.reports.data.model.UserStats;

import java.util.Date;

public class DbUserDataLayer implements UserDataLayer {
    @Override
    public UserStats getStats(String user, String repo, Date from, Date to) {
        return null;
    }
}
